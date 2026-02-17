package org.example.app

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var statusText: TextView
    private lateinit var restartButton: Button
    private lateinit var cells: List<Button>

    private var board: CharArray = CharArray(9) { EMPTY }
    private var currentPlayer: Char = PLAYER_X
    private var gameOver: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        restartButton = findViewById(R.id.restartButton)

        cells = listOf(
            findViewById(R.id.cell0),
            findViewById(R.id.cell1),
            findViewById(R.id.cell2),
            findViewById(R.id.cell3),
            findViewById(R.id.cell4),
            findViewById(R.id.cell5),
            findViewById(R.id.cell6),
            findViewById(R.id.cell7),
            findViewById(R.id.cell8)
        )

        for (i in cells.indices) {
            cells[i].setOnClickListener { onCellTapped(i) }
        }

        restartButton.setOnClickListener { resetGame() }

        resetGame()
    }

    private fun onCellTapped(index: Int) {
        if (gameOver) return
        if (board[index] != EMPTY) return

        board[index] = currentPlayer
        renderBoard()

        val winner = findWinner(board)
        when {
            winner != null -> {
                gameOver = true
                setStatusWinner(winner)
                setBoardEnabled(false)
            }

            isDraw(board) -> {
                gameOver = true
                statusText.text = "Draw"
                statusText.setTextColor(getColorCompat(R.color.app_secondary))
                setBoardEnabled(false)
            }

            else -> {
                currentPlayer = otherPlayer(currentPlayer)
                setStatusTurn(currentPlayer)
            }
        }
    }

    private fun resetGame() {
        board = CharArray(9) { EMPTY }
        currentPlayer = PLAYER_X
        gameOver = false

        renderBoard()
        setBoardEnabled(true)
        setStatusTurn(currentPlayer)
    }

    private fun renderBoard() {
        for (i in 0 until 9) {
            val value = board[i]
            cells[i].text = if (value == EMPTY) "" else value.toString()
            cells[i].isEnabled = !gameOver && value == EMPTY
        }
    }

    private fun setBoardEnabled(enabled: Boolean) {
        for (i in 0 until 9) {
            val value = board[i]
            cells[i].isEnabled = enabled && (value == EMPTY)
        }
    }

    private fun setStatusTurn(player: Char) {
        statusText.text = "${player}'s turn"
        statusText.setTextColor(getColorCompat(R.color.app_secondary))
    }

    private fun setStatusWinner(player: Char) {
        statusText.text = "$player wins"
        // Use the style-guide success accent for a win.
        statusText.setTextColor(getColorCompat(R.color.app_success))
    }

    private fun getColorCompat(colorRes: Int): Int {
        return resources.getColor(colorRes, theme)
    }

    private fun otherPlayer(player: Char): Char = if (player == PLAYER_X) PLAYER_O else PLAYER_X

    private fun isDraw(board: CharArray): Boolean = board.all { it != EMPTY } && findWinner(board) == null

    private fun findWinner(board: CharArray): Char? {
        val lines = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6)
        )

        for (line in lines) {
            val a = line[0]
            val b = line[1]
            val c = line[2]
            val v = board[a]
            if (v != EMPTY && v == board[b] && v == board[c]) {
                return v
            }
        }
        return null
    }

    private companion object {
        private const val EMPTY: Char = '\u0000'
        private const val PLAYER_X: Char = 'X'
        private const val PLAYER_O: Char = 'O'
    }
}
