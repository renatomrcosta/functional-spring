### Functional style Spring playground

Using this repo to explore some of the new functional avenues for spring development, such as routing, application runner and so on.

Along with it, also trying out localstack for containerizing local development, and also experimenting with coroutines (and setting `-Dkotlinx.coroutines.debug` as a VM Option to better trace issues) and Loom as Dispatchers.



### Current thoughts

- I enjoy the functional style of routing, but the tooling does not look like it is there quite yet: 
    -  The endpoints tool window, and the missing gutter icons when using the DSLs are a bit of a pain. I miss the ability to navigate to the handler from the route declaration easily, and generate HTTP calls in the editor.
- Both the suspend version and the `coRouter` version are backed by the `reactor-http-nio` threadpool, which is nice.

- Regarding Loom
  - After setting up JDK 20 and the `--enable-preview` VM option, I was good to go. Creating a thread pool is essentially the exact same as the existing methods as before (using the `Executors.newVirtualThreadExecutor()` factory method).
  - Loom-backed coroutine context are really great substitutes for the `Dispatchers.IO` for Long thread-blocking workhorses: because it changes the underlying thread pool to be virtual in nature, existing blocking operations get the benefits of waiting gracefully, while we can still take advantage of the superpowers of Structured concurrency at the top level.
  - We have, unfortunately, the tradeoff of threadpool switching (since `Dispatchers.Default` and `IO` share threads)
  - 