# plumula diff

[](dependency)
```clojure
[plumula/diff "0.1.1"] ;; latest release
```
[](/dependency)

A _fast_ text diff library for Clojure and ClojureScript.

This is a thin Clojure(Script) wrapper around the Java(Script) versions of the
[Diff, Match and Patch library][diff-match-patch] by Neil Fraser.


## Usage

```clojure
(require '[plumula.diff :as d])

(d/diff "abcd" "bcde")
; => [{::d/operation ::d/delete, ::d/text "a"}
;     {::d/operation ::d/equal,  ::d/text "bcd"}
;     {::d/operation ::d/insert, ::d/text "e"}]
```

`diff` can take optional keyword-arguments which are described in the following
sections.


### Spec

If you are using Clojure(Script) 1.9, feel free to peruse the nice specs in
`plumula.diff.spec`.


### Optimising the output for readability

In its default mode, `diff` will try to produce an output that is minimal, at
the expense of readability.

Set the `cleanup` option to `cleanup-semantic` to make the output more readable
to humans, at the expense of minimality. This will (roughly)

- Eliminate small spurious equalities (AKA ‘chaff’) between the documents 
- Aligning the operations to word-boundaries if possible

```clojure
(d/diff document-1 document-2 ::d/cleanup ::d/cleanup-semantic)
```


### Optimising the output for efficiency

In terms of storage and computation, each operation in a diff output is likely
to have a cost with a constant component, and a component that is proportional
to the operation’s lenght in number of characters.

Because of this constant per operation cost, a diff output can be made more
efficient by sacrificing minimality (in terms of number of characters edited)
in order to reduce the number of editing operations. This can be achieved by
settind the `cleanup` option to `cleanup-efficiency`:

```clojure
(d/diff document-1 document-2 ::d/cleanup ::d/cleanup-efficiency ::edit-cost 4)
```

The relative cost of an edit operation in terms of edited characters can be
tuned with the `edit-cost` option. At the default setting of 4, the optimiser
will accept to increase the character-count of the diff by up to 4 characters in
order to save an edit operation.


### Optimising the output while keeping a minimal diff (bad idea)

If you don’t like the idea of sacrificing minimality, but still want to improve
the readability or efficiency of diff’s output, there are options for that.
They are poor compromises, though, because the minimality constraint doesn’t
leave enough wiggle room for the optimising algorithm to do a great job.

To improve the readability of the output while keeping it minimal set the 
`cleanup` option to `cleanup-semantic-lossless`, but remember that in order to
keep the output minimal, the algorithm has to leave the semantic chaff there,
and readability won’t be as great as with `cleanup-semantic`.

To reduce the number of edit operations in the output while keeping the  number
of characters minimal, set the `cleanup` option to `cleanup-merge`. This might
make sense if the cost of an edit is negligible vs the cost of a character in
the output. It could be argued that there isn’t much of a point in reducing the
number of edit operations at all in that case though.


### Disabling the line-diff optimisation

To speed up computations, `diff` will pre-process the texts by diffing them
line-by-line. On long documents with multiple small edits, this can lead to an
order of magnitude improvement in speed and memory consumption. However, there
is a risk that this optimisation may result producting in a non-minimal diff
output.

If you don’t want to take that risk, use the `check-lines` option to disable
the optimisation:

```clojure
(d/diff document-1 document-2 ::d/check-lines false)
```


### Changing the maximum run-time

The diffing process consists of relatively fast pre- and post-processing
steps surrounding a potentially long-running diff-optimising step. By default,
this optimising step will time out after a run time of 1 second, returning a
correct but potentially non-minimal diff output.

The `timeout` option lets you set another maximum run time for the optimising
step, or disable the time out behaviour entirely, allowing for an arbitrarily
long optimising step:

```clojure
; time out after 1.5 seconds
(d/diff document-1 document-2 ::d/timeout 1.5)

; never time out
(d/diff document-1 document-2 ::d/timeout 0)
```

## Known limitations

- The `match` and `patch` functions are not currently wrapped
- Depends on both the Java and JavaScript library, even if your project targets
  only one of Clojure or ClojureScript. The unneeded dependency will get
  compiled away but it’s still a useless download.


## Change log

The notable changes to this project are documented in the
[change log](CHANGELOG.md).


## License

Distributed under the [Apache License, Version 2.0](LICENSE.txt).
Copyright &copy; 2017 [Frederic Merizen][frederic-merizen].

The underlying [Diff, Match and Patch Library][diff-match-patch] is distributed
under the [Apache License, Version 2.0][apache-2-license] and copyright &copy;
2006 Google Inc.

[apache-2-license]: http://www.apache.org/licenses/LICENSE-2.0
[diff-match-patch]: https://code.google.com/archive/p/google-diff-match-patch/
[frederic-merizen]: https://www.linkedin.com/in/fredericmerizen/
