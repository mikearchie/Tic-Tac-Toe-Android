package com.archambault.tic_tac_toe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private Button gameActionButton;
    private Button[][] ticTacToeGrid = new Button[3][3];
    private TextView gameStatusTextView;
    private boolean isOsTurn;
    private String gameStatus;

    // define the SharedPreferences object
    private SharedPreferences savedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameActionButton = (Button) findViewById(R.id.gameActionButton);
        gameActionButton.setOnClickListener(this);

        gameStatusTextView = (TextView) findViewById(R.id.gameStatusTextView);
        gameStatus = "Press New Game to start!";
        gameStatusTextView.setText(gameStatus);

        //assign each button
        //top row
        ticTacToeGrid[0][0] = (Button) findViewById(R.id.upLeft);
        ticTacToeGrid[0][1] = (Button) findViewById(R.id.upMid);
        ticTacToeGrid[0][2] = (Button) findViewById(R.id.upRight);

        //middle row
        ticTacToeGrid[1][0] = (Button) findViewById(R.id.midLeft);
        ticTacToeGrid[1][1] = (Button) findViewById(R.id.midMid);
        ticTacToeGrid[1][2] = (Button) findViewById(R.id.midRight);

        //bottom row
        ticTacToeGrid[2][0] = (Button) findViewById(R.id.bottomLeft);
        ticTacToeGrid[2][1] = (Button) findViewById(R.id.bottomMid);
        ticTacToeGrid[2][2] = (Button) findViewById(R.id.bottomRight);

        for (Button[] gridRow:ticTacToeGrid) {
            for (Button currButton:gridRow) {
                currButton.setOnClickListener(this);
            }
        }

        // get SharedPreferences object
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
    }

    @Override
    public void onPause() {

        String gridString = "";

        // save the instance variables
        Editor editor = savedValues.edit();

        String currButton;
        for (int col=0; col<=2; col++) {
            for (int row = 0; row <= 2; row++) {
                currButton = ticTacToeGrid[row][col].getText().toString();
                //store a space for empty buttons
                if (currButton.equals(""))
                    currButton = " ";
                gridString = gridString + currButton;
            }
        }

        editor.putString("gridString", gridString);
        editor.putString("gameStatus", gameStatus);
        editor.putBoolean("isOsTurn", isOsTurn);

        editor.apply();

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        String gridString = "";
        // retrieve the saved values
        gridString = savedValues.getString("gridString", "");
        gameStatus = savedValues.getString("gameStatus", "");
        isOsTurn = savedValues.getBoolean("isOsTurn", false);


        // set the values on game status widget
        gameStatusTextView.setText(gameStatus);

        // set the text for each button in the grid
        int counter = 0;
        String currButtonText = "";
        for (int col=0; col<=2; col++) {
            for (int row = 0; row <= 2; row++) {
                //currButtonText = (gridString.substring(counter, counter + 1));

                currButtonText = (gridString.substring(counter, counter + 1));

                if (currButtonText.equals(" ")) {
                    //enable blank buttons, and ensure text is empty string
                    ticTacToeGrid[row][col].setEnabled(true);
                    currButtonText = "";
                }
                else
                    //disable when button is X or O
                    ticTacToeGrid[row][col].setEnabled(false);

                //update button text
                ticTacToeGrid[row][col].setText(currButtonText);
                counter++;
            }
        }
        //check if current match is done ensure the board is synced with the current game state.
        //specifically, this is required to disable the buttons if the game is already won.
        checkMatchDone();
    }

    private void startNewGame() {
        clearGrid();
        for (Button[] gridRow:ticTacToeGrid) {
            for (Button currButton:gridRow) {
                currButton.setEnabled(true);
            }
        }
        isOsTurn = false;
        gameStatus = "X's turn";
        gameStatusTextView.setText(gameStatus);
    }

    private void clearGrid() {
        for (Button[] gridRow:ticTacToeGrid) {
            for (Button currButton:gridRow) {
                currButton.setText("");
            }
        }
    }

    private void disableGrid() {
        for (Button[] gridRow:ticTacToeGrid) {
            for (Button currButton:gridRow) {
                currButton.setEnabled(false);
            }
        }
    }

    private void makePlay(int row, int col) {
        if (isOsTurn)
            ticTacToeGrid[row][col].setText("O");
        else
            ticTacToeGrid[row][col].setText("X");
        ticTacToeGrid[row][col].setEnabled(false);
        isOsTurn = !isOsTurn;
        if (isOsTurn)
            gameStatus = "O's turn";
        else
            gameStatus = "X's turn";
        checkMatchDone();
        gameStatusTextView.setText(gameStatus);
    }

    private void checkMatchDone(){

        boolean emptySquareExists = false;
        String gameStatusTemp = gameStatus;

        //first check if the board is fully played
        for (Button[] gridRow:ticTacToeGrid) {
            for (Button currButton:gridRow) {
                if (currButton.getText() == "")
                    emptySquareExists = true;
            }
        }

        if (!emptySquareExists) {
            //default to Cat's game. If somebody won, this will get overwritten below.
            gameStatusTemp = "Cat's game!";
            disableGrid();
        }

        //check for win by row
        for (Button[] gridRow:ticTacToeGrid) {
            if (!gridRow[0].getText().equals("")
                    && gridRow[0].getText().equals(gridRow[1].getText())
                    && gridRow[1].getText().equals(gridRow[2].getText())) {
                //won by row
                gameStatusTemp = gridRow[0].getText() + " won!";
                disableGrid();
            }
        }

        //check by column
        for (int col=0; col<=2; col++) {
            if (!ticTacToeGrid[0][col].getText().equals("")
                    && ticTacToeGrid[0][col].getText().equals(ticTacToeGrid[1][col].getText())
                    && ticTacToeGrid[1][col].getText().equals(ticTacToeGrid[2][col].getText())) {
                //won by column
                gameStatusTemp = ticTacToeGrid[0][col].getText() + " won!";
                disableGrid();
            }
        }

        //check diagonals
        if (!ticTacToeGrid[1][1].getText().equals("")
                && ((ticTacToeGrid[0][0].getText().equals(ticTacToeGrid[1][1].getText())
                        && ticTacToeGrid[1][1].getText().equals(ticTacToeGrid[2][2].getText())
                    || (ticTacToeGrid[0][2].getText().equals(ticTacToeGrid[1][1].getText())
                        && ticTacToeGrid[1][1].getText().equals(ticTacToeGrid[2][0].getText()))))) {
                //won diagonally
                gameStatusTemp = ticTacToeGrid[1][1].getText() + " won!";
                disableGrid();
        }

        gameStatus = gameStatusTemp;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gameActionButton:
                startNewGame();
                break;
            case R.id.upLeft:
                makePlay(0,0);
                break;
            case R.id.upMid:
                makePlay(0,1);
                break;
            case R.id.upRight:
                makePlay(0,2);
                break;
            case R.id.midLeft:
                makePlay(1,0);
                break;
            case R.id.midMid:
                makePlay(1,1);
                break;
            case R.id.midRight:
                makePlay(1,2);
                break;
            case R.id.bottomLeft:
                makePlay(2,0);
                break;
            case R.id.bottomMid:
                makePlay(2,1);
                break;
            case R.id.bottomRight:
                makePlay(2,2);
                break;
        }
    }
}
