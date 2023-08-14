## Inference Engine

1. This engine uses [unit-preference](https://dl.acm.org/doi/pdf/10.1145/1464052.1464109).
2. There are several simplification that are made.
    - There will **not** be more than one implication (=>) in each input KB sentence.
    - There will **not** be more than one Predicate/Atomic Sentence after each implication.
    - The order of operators is: ~, &, |, =>
    - Each predicate will have between 1 and 25 constant arguments.
    - Two or more arguments will be separated by commas.
    - There will be NO parentheses in the input to the KB except to mark predicate arguments. For example: Pred(x,y) is allowed, but A & (B | C) is not.
    - A given predicate name will not appear with different number of arguments.
    - There will be at most 100 sentences in the knowledge base.
3. More information on input pattern can be found in `Homework 3.pdf`

## Instruction on preparing input file

1. The first line contains a query as one logic sentence (further detailed below).
2. The line after contains an integer K specifying the number of sentences given for the knowledge base.
3. The remaining K lines contain the sentences for the knowledge base, one sentence per line.

## Output format

1. If engine is able to infer input query then it returns `TRUE`, otherwise `FALSE`.

## Time limit

1. I have currently configured 200 secs per test case as the time limit in `is_time_over` function.
2. After time limit exceeds, engine returns `FALSE`.

## Run homework.py on single input

1. Create `input.txt` as per the instruction above in the root folder.
2. Run `python3 homework.py`.
3. Inference engine's output can be found in `output.txt`.
