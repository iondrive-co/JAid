Implementations of some useful concepts for writing performant java code.

 - LinkedArrayHashSet - A data structure combining the java.util List and Set interfaces using an indexed unrolled linked list.
 - ReversiblePerfectHash - Reversible String -> long function that does not require storage of the Strings.
 - Geometric Random - An extremely fast generator of random numbers from a geometric series. Very useful when implementing a high performance CSL.

Some benchmarking code and results are included, although are no replacement for running these in your own environment with a good microbenchmarking library like JMH.
