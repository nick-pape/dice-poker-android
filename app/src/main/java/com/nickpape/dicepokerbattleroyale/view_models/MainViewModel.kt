package com.nickpape.dicepokerbattleroyale.view_models

import android.provider.MediaStore.Audio.Media
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseUser
import com.nickpape.dicepokerbattleroyale.adapters.PlayerScore
import com.nickpape.dicepokerbattleroyale.api.ViewModelDBHelper
import com.nickpape.dicepokerbattleroyale.auth.FirestoreAuthLiveData
import com.nickpape.dicepokerbattleroyale.models.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class MainViewModel: ViewModel() {
    // Database access
    private val dbHelp = ViewModelDBHelper()

    // ================= User Authentication =====================
    var firebaseAuthLiveData = FirestoreAuthLiveData()
    fun updateUser() {
        firebaseAuthLiveData.updateUser()
    }
    // ===========================================================


    // ====================== Games ==============================
    private var _games = MutableLiveData<MutableList<Game>>()

    fun fetchAllGames() {
        dbHelp.fetchAllGames(_games) {
            _fetchDone.postValue(true)
        }
    }

    fun games(): LiveData<MutableList<Game>> {
        return _games
    }

    // Get a note from the memory cache
    fun getGame(position: Int) : Game {
        val game = _games.value?.get(position)
        return game!!
    }

    fun createGame(playerIds: Set<String>, onSuccess: (game: Game) -> Unit) {
        val currentPlayerId = firebaseAuthLiveData.getCurrentUser()!!.uid
        val copy = HashSet<String>(playerIds)
        copy.remove(currentPlayerId)
        val playerIdList = ArrayList<String>(copy)
        playerIdList.add(0, currentPlayerId)

        dbHelp.createNewGame(playerIdList) {
            val games = _games.value!!
            games.add(it)
            _games.postValue(games)
            onSuccess(it)
        }
    }

    private var _currentPlayer: LiveData<Player>? = null
    fun currentPlayerInGame(): LiveData<Player> {
        if (_currentPlayer == null) {
            _currentPlayer = PairedLiveData<Player, Game, HashMap<String, Player>>(currentGame(), playersMap()) {
                game, players ->
                return@PairedLiveData players[game.playerIds[game.currentPlayerIndex]]!!
            }
        }
        return _currentPlayer!!
    }

    private var _isActiveUserTurn: LiveData<Boolean>? = null
    fun isActiveUserTurn(): LiveData<Boolean> {
        if (_isActiveUserTurn == null) {
            _isActiveUserTurn = PairedLiveData<Boolean, Player, FirebaseUser?>(currentPlayerInGame(), firebaseAuthLiveData) {
                currentPlayer, currentUser ->
                return@PairedLiveData currentPlayer.id == currentUser?.uid
            }
        }
        return _isActiveUserTurn!!
    }

    private var _currentSelectedScorecardIsCurrentUser: LiveData<Boolean>? = null
    fun currentSelectedScorecardIsCurrentUser(): LiveData<Boolean> {
        if (_currentSelectedScorecardIsCurrentUser == null) {
            _currentSelectedScorecardIsCurrentUser = PairedLiveData<Boolean, String, FirebaseUser?>(selectedPlayer(), firebaseAuthLiveData) {
                selectedPlayerId, currentUser ->
                return@PairedLiveData selectedPlayerId == currentUser?.uid
            }
        }
        return _currentSelectedScorecardIsCurrentUser!!
    }

    private var _shouldShowDice: LiveData<Boolean>? = null
    fun shouldShowDice(): LiveData<Boolean> {
        if (_shouldShowDice == null) {
            _shouldShowDice = PairedLiveData<Boolean, Boolean, Boolean>(isActiveUserTurn(), currentSelectedScorecardIsCurrentUser()) {
                isActiveUserTurn, currentSelectedScorecardIsCurrentUser ->
                return@PairedLiveData isActiveUserTurn && currentSelectedScorecardIsCurrentUser
            }
        }
        return _shouldShowDice!!
    }

    // ===========================================================

    // =================== Players ==========================
    data class PlayerSelection(
        val player: Player,
        var isSelected: Boolean = false
    )

    private var _players = MutableLiveData<HashMap<String, Player>>()

    private var _selectedPlayers = MutableLiveData(HashSet<String>())
    fun selectedPlayers(): LiveData<HashSet<String>> {
        _selectedPlayers.value!!.add(firebaseAuthLiveData.getCurrentUser()!!.uid)
        _selectedPlayers.value = _selectedPlayers.value
        return _selectedPlayers
    }

    fun fetchAllPlayers() {
        dbHelp.fetchAllPlayers(_players) {
            _fetchDone.postValue(true)
        }
    }

    public fun playersMap(): LiveData<HashMap<String, Player>> {
        return _players
    }

    private var _playerSelections: LiveData<List<PlayerSelection>>? = null
    fun playersList(): LiveData<List<PlayerSelection>> {
        if (_playerSelections == null) {
            val playerSelections = MediatorLiveData<List<PlayerSelection>>()
            playerSelections.addSource(_players) { players ->
                val selected = selectedPlayers().value!!

                playerSelections.postValue(players.values
                    .filter { it.id != firebaseAuthLiveData.getCurrentUser()?.uid }
                    .map { player ->
                        PlayerSelection(player, selected.contains(player.id))
                    })
            }

            playerSelections.addSource(selectedPlayers()) { selectedPlayers ->
                if (playerSelections.value !== null) {
                    playerSelections.value!!.forEach {
                        Log.d(javaClass.simpleName,"Is ${it.player.id} selected? ${selectedPlayers.contains(it.player.id)}")

                        it.isSelected = selectedPlayers.contains(it.player.id)
                    }
                    playerSelections.postValue(playerSelections.value!!)
                }
            }

            _playerSelections = playerSelections
        }

        return _playerSelections!!
    }

    fun toggleSelectPlayer(player: Player) {
        val selectedPlayersSet = _selectedPlayers.value!!
        val key = player.id
        if (selectedPlayersSet.contains(key)) {
            selectedPlayersSet.remove(key)
            Log.d(javaClass.simpleName, "Unselected player $key")
        } else {
            selectedPlayersSet.add(key)
            Log.d(javaClass.simpleName, "Selected player $key")
        }
        _selectedPlayers.value = selectedPlayersSet
    }

    // Get a note from the memory cache
    fun getPlayer(position: Int) : PlayerSelection {
        Log.d(javaClass.simpleName, "Getting player at ${position}")

        val player = playersList().value?.get(position)
        return player!!
    }

    fun addOrUpdatePlayer(player: Player) {
        dbHelp.addOrUpdatePlayer(player)
    }
    // ===========================================================



    // ===========================================================
    //                GAME SPECIFIC STUFF
    // ===========================================================
    private var _playerScoreSheet: MediatorLiveData<ScoreSheet>? = null
    fun playerScoreSheet(): LiveData<ScoreSheet> {
        if (_playerScoreSheet == null) {
            val result = MediatorLiveData<ScoreSheet>()
            result.addSource(_selectedPlayer) {
                Log.d(javaClass.simpleName, "Selecting player $it scoresheet from ${playerScoreSheet().value}")
                result.postValue(playerScoreSheets().value?.get(it))
            }

            result.addSource(playerScoreSheets()) {
                Log.d(javaClass.simpleName, "Selecting player ${_selectedPlayer.value} scoresheet from $it")
                result.postValue(it?.get(_selectedPlayer.value))
            }
            _playerScoreSheet = result
        }
        return _playerScoreSheet!!
    }

    private var _fetchDone = MutableLiveData<Boolean>(false)
    fun fetchDone(): LiveData<Boolean> {
        return _fetchDone
    }

    private var _playerScoreSheets: MediatorLiveData<HashMap<String, ScoreSheet>>? = null
    fun playerScoreSheets(): LiveData<HashMap<String, ScoreSheet>> {
        if (_playerScoreSheets == null) {
            val result = MediatorLiveData<HashMap<String, ScoreSheet>>()

            result.addSource(currentGame()) {
                dbHelp.fetchAllScoreSheets(it, result) {
                    _fetchDone.postValue(true)
                }
            }

            _playerScoreSheets = result
        }
        return _playerScoreSheets!!
    }

    fun getPlayerNameFromId(playerId: String): String {
        val user = firebaseAuthLiveData.getCurrentUser()
        if (user == null) {
            return ""
        }

        return if (playerId == user.uid) {
            "You"
        } else {
            playersMap().value!![playerId]!!.display_name
        }
    }

    private var _playerScores: LiveData<List<PlayerScore>>? = null
    fun playerScores(): LiveData<List<PlayerScore>> {
        if (_playerScores == null) {
            val result = MediatorLiveData<List<PlayerScore>>()

            result.addSource(playerScoreSheets()) { scoresheets ->
                if (scoresheets != null) {
                    result.postValue(scoresheets.entries.map { scoresheet ->
                        Log.d(javaClass.simpleName,"Updating player ${scoresheet.key} to ${scoresheet.value.getScore()}")

                        return@map PlayerScore(
                            scoresheet.key,
                            getPlayerNameFromId(scoresheet.key),
                            scoresheet.value.getScore(),
                            scoresheet.key == selectedPlayer().value
                        )
                    })
                }
            }

            _playerScores = result
        }
        return _playerScores!!
    }

    private val _selectedPlayer: MutableLiveData<String> = MutableLiveData()
    fun selectedPlayer(): LiveData<String> {
        return _selectedPlayer
    }

    fun selectPlayer(playerId: String) {
        _selectedPlayer.postValue(playerId)
    }

    private val _currentGame: MutableLiveData<Game> = MutableLiveData()
    fun setGameById(gameId: String) {
        _currentGame.postValue(games().value!!.find {
            it.firestoreID == gameId
        })
    }

    fun currentGame(): LiveData<Game> {
        return _currentGame
    }

    fun isGameOver(): LiveData<Boolean> {
        val result = MediatorLiveData<Boolean>()
        result.value = false

        result.addSource(playerScoreSheet()) { value ->
            if (value == null) {
                result.postValue(false)
                return@addSource
            }

            result.postValue(value.isGameOver())
        }

        return result
    }

    // TODO() - have this be calculated only after dice rolls
    fun observePotentialScores(): LiveData<RawScoreSheet> {
        val result = MediatorLiveData<RawScoreSheet>()

        result.addSource(playerDice) { value ->
            result.postValue(RawScoreSheet.getPotentialScores(value))
        }

        result.addSource(diceCount) {
            if (it == 0) {
                result.postValue(null)
            }
        }

        return result
    }

    private val playerDice = MutableLiveData<List<DiceRoll>>(listOf(
        DiceRoll(null),
        DiceRoll(null),
        DiceRoll(null),
        DiceRoll(null),
        DiceRoll(null)
    ))

    fun resetDice() {
        diceCount.value = 0
        playerDice.value = listOf(
            DiceRoll(null),
            DiceRoll(null),
            DiceRoll(null),
            DiceRoll(null),
            DiceRoll(null)
        )
    }

    fun observePlayerDice(): LiveData<List<DiceRoll>> {
        return playerDice
    }

    private val random = Random(System.currentTimeMillis())
    private fun randomDice(): Int {
        return random.nextInt(6) + 1
    }

    fun toggleHoldDice(index: Int) {
        playerDice.value!![index].isHeld = !playerDice.value!![index].isHeld
        playerDice.postValue(playerDice.value)
    }

    private val diceCount: MutableLiveData<Int> = MutableLiveData(0)
    fun observeDiceCount(): LiveData<Int> {
        return diceCount
    }

    fun canRollAgain(): LiveData<Boolean> {
        val result = MediatorLiveData<Boolean>()

        result.addSource(diceCount) {
            result.postValue(it < 3 && isGameOver().value == false)
        }

        result.addSource(isGameOver()) {
            result.postValue(diceCount.value!! < 3 && !it)
        }

        return result
    }

    fun rollDice() {
        val newDice = playerDice.value!!.map {
            if (it.isHeld) {
                return@map it
            }

            return@map DiceRoll(randomDice())
        }

        playerDice.value = newDice
        diceCount.value = diceCount.value?.plus(1)
    }

    fun updateScoresheet(field: ScoreableField, value: Int, withBonusYahtzee: Boolean) {
        val game = currentGame().value!!
        val scoresheet = _playerScoreSheet!!.value!!

        if (withBonusYahtzee) {
            scoresheet.addBonusYahtzee()
        }

        scoresheet.setField(field, value)
        game.nextPlayer()

        dbHelp.updateScoreSheetAndGame(game, _playerScoreSheet!!.value!!) {
            refreshGame()
            resetDice()
        }
    }

    fun refreshGame() {
        dbHelp.refreshGame(_currentGame)
    }

    // ===========================================================
    // ===========================================================
}