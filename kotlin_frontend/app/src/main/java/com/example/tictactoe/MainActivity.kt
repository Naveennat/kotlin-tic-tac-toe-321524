package com.example.tictactoe

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var restartButton: Button

    private val cellButtons: Array<Button> = Array(9) { Button(null) }

    // Game state: 0..8 cells storing "X", "O", or null
    private val board: Array<String?> = Array(9) { null }
    private var currentPlayer: String = PLAYER_X
    private var gameOver: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        restartButton = findViewById(R.id.restartButton)

        // Collect cell buttons by id (cell0..cell8)
        for (i in 0..8) {
            val resId = resources.getIdentifier("cell$i", "id", packageName)
            cellButtons[i] = findViewById(resId)
            cellButtons[i].setOnClickListener { onCellTapped(i) }
        }

        restartButton.setOnClickListener { resetGame() }

        // Restore state on rotation
        if (savedInstanceState != null) {
            currentPlayer = savedInstanceState.getString(STATE_CURRENT_PLAYER, PLAYER_X) ?: PLAYER_X
            gameOver = savedInstanceState.getBoolean(STATE_GAME_OVER, false)
            val savedBoard = savedInstanceState.getStringArray(STATE_BOARD)
            if (savedBoard != null && savedBoard.size == 9) {
                for (i in 0..8) {
                    board[i] = savedBoard[i]
                }
            }
        }

        render()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_CURRENT_PLAYER, currentPlayer)
        outState.putBoolean(STATE_GAME_OVER, gameOver)
        outState.putStringArray(STATE_BOARD, Array(9) { i -> board[i] })
    }

    private fun onCellTapped(index: Int) {
        if (gameOver) return
        if (board[index] != null) return

        board[index] = currentPlayer

        val winner = checkWinner()
        if (winner != null) {
            gameOver = true
            statusText.text = getString(R.string.status_winner, winner)
            // Disable further taps visually
            setBoardEnabled(false)
            renderBoardOnly()
            return
        }

        if (isDraw()) {
            gameOver = true
            statusText.text = getString(R.string.status_draw)
            setBoardEnabled(false)
            renderBoardOnly()
            return
        }

        // Switch player
        currentPlayer = if (currentPlayer == PLAYER_X) PLAYER_O else PLAYER_X
        render()
    }

    private fun resetGame() {
        for (i in 0..8) {
            board[i] = null
        }
        currentPlayer = PLAYER_X
        gameOver = false
        setBoardEnabled(true)
        render()
    }

    private fun render() {
        renderBoardOnly()
        statusText.text = getString(R.string.status_turn, currentPlayer)
    }

    private fun renderBoardOnly() {
        for (i in 0..8) {
            cellButtons[i].text = board[i] ?: ""
        }
    }

    private fun setBoardEnabled(enabled: Boolean) {
        for (i in 0..8) {
            cellButtons[i].isEnabled = enabled && board[i] == null
            cellButtons[i].alpha = if (cellButtons[i].isEnabled) 1.0f else 0.85f
        }
    }

    private fun isDraw(): Boolean {
        // Draw when all cells filled and no winner
        for (i in 0..8) {
            if (board[i] == null) return false
        }
        return checkWinner() == null
    }

    private fun checkWinner(): String? {
        val wins = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6)
        )

        for (line in wins) {
            val a = board[line[0]]
            val b = board[line[1]]
            val c = board[line[2]]
            if (a != null && a == b && a == c) return a
        }
        return null
    }

    private companion object {
        private const val PLAYER_X = "X"
        private const val PLAYER_O = "O"

        private const val STATE_CURRENT_PLAYER = "state_current_player"
        private const val STATE_GAME_OVER = "state_game_over"
        private const val STATE_BOARD = "state_board"
    }
}
