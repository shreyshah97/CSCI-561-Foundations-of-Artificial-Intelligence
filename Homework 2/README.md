## About

Minimax AI agent with alpha-beta pruning and heuristic function [pente.org](https://pente.org/)

## Instruction on preparing input file

| Line Number   | Description                                                                                                                                                                                                                                                                 |
| ------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| First Line    | A string BLACK or WHITE indicating which color you play. White will always start the game.                                                                                                                                                                                  |
| Second Line   | A strictly positive floating point number indicating the amount of play time remaining for your agent (in seconds).                                                                                                                                                         |
| Third Line    | Two non-negative 32-bit integers separated by a comma indicate the number of pieces captured by White and Black players consecutively. Caution, it will always be ordered as first captured by White, then by Black, irrespective of what color is given in the first line. |
| Next 19 lines | Description of the game board, with 19 lines of 19 symbols each: - w for a cell occupied by a white piece - b for a cell occupied by a black piece - . (a dot) for an empty intersection                                                                                    |

## How to run?

1. Step 1: Calibrate
    - This step adjusts the timing between the agent and the machine. If the computer's processing power is high, then depth two search can be done in 0.2 seconds, and depth three search can be done in 1 minute on average.
    - Since we are capping the total game time to 5 minutes, we need to make sure when to search on depth three and when on depth 2.
    - Run the `calibrate.java` file. It will generate a `calibrate.txt` file. This file will be used later in `homework.java`.
2. Step 2: Run manager
    - The manager coordinates between the agent and the actual move against the agent.
    - The manager will ask the user to enter the move.
    - Manager then runs the agent and gets back to the user with the move agent played.
    - This infinite loop code will stop if either time is up or one player wins.

## Run homework.py on single input

1. Create `input.txt` as per the instruction above in the root folder.
2. Run the `homework.java` file.
3. Next best move can be found in `output.txt`
