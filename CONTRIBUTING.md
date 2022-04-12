# Contributing to Stensl: *Contributing Guidelines for a1.0.0*

First of all, thank you for taking the time to help improve Stensl! 

Whether you want to add to the interpreter yourself, want to request a feature, or just have a question, your time is valuable and your contributions are appreciated.

## Table of Contents

- [Find a bug?](find-a-bug?)
- [Want to Suggest a Feature?](want-to-suggest-a-feature)
   - [Creating a New Suggestion](creating-a-new-suggestion)
   - [Ammending an Existing Suggestion](ammending-an-existing-suggestion)
   - [Endorsing an Existing Suggestion](endorsing-an-existing-suggestion)
- [Want to Make a Direct Contribution?](want-to-make-a-direct-contribution)

## Find a Bug?

Please use the issues page for all bugs in Stensl with the Stensl interpreter. Do NOT use this page for bugs that occur in your code but are not the result of a fault in the interpreter. For instance: if your code throws an error on a line containing `print(2/0)`, you should not report this bug because a division by 0 error is expected. However, if your code throws an error on a line containing `print(2+2)` (or prints some number that is not '4'), then you should absolutely report this, because it is likely a problem with the interpreter itself.

Before creating a bug report, please check that your issue is unique. This can be done by going to the issues page and searching for open bugs that match yours. If the bug you're experiencing seems to be the result of an already-known bug in the interpreter, please add a comment to the existing issue with your code and the nature of your bug. This will help us reproduce and understand the error's behavior.

If you've performed a search, and your bug is unique, then please create a new post under the issues tab, marked with the "bug" tag. In your post, please be as specific and detailed as possible. Ideally, include a complete copy of your code or a link to it. Please also provide the output which your code generates, including errors, and how this differs from the expected output. If you've been able to reproduce this bug under other circumstances, please provide information on these as well. If your bug causes the interpeter to crash, please add the crash tag as well.

## Want to Suggest a Feature?

If you'd love to see a new feature be introduced to the Stensl language or its interpreter, you can submit a request through the Github issues page for this repo. Please use the "enhancement" tag for your request. Before making a suggestion, however, please do a preliminary search to see if another request has been made which matches your own suggestion. If there is a request which is similar to yours, you may comment on that suggestion to [endorse](#endorsing-an-existing-suggestion) it, or to offer an [ammendment](#ammending-an-existing-suggestion) that you believe would improve the suggestion. Otherwise, please [create a new suggestion](#creating-a-new-suggestion).

### Creating a New Suggestion

When suggesting a new feature, please include the following:

- A clear and descriptive title for your suggestion
- An indication of whether you are suggesting an improvement to the interpreter, or the Stensl language itself
- A description of the need for your new feature, including at least one specific use case, with as many details as possible
- A description of how someone would use your new feature (e.g. code snippets), with as many details as possible
- An outline of the exact behavior of your new feature, again with as many details as possible, ideally including examples
- A list of other langauges or interpreters with this feature, if any
- A description of how your feature is different from any similar previously suggested features, if any

### Ammending an Existing Suggestion

Occasionally, you may generally support a proposed feature but have a few qualms with some of its qualities. For instance, you may want to change its usage syntax or its behavior in certain cases. In this case, please comment on the page containing the suggestion you'd like to ammend with all of the following:

- How you would change the proposed feature, being as specific as possible
- How your changes would improve the proposal, with as many details as possible, ideally with examples
- Any potential downsides to your ammendment, if you are aware of any

### Endorsing an Existing Suggestion

If you see a proposed feature or an ammendment to a proposed feature that you would like to see added, please leave a comment on the page containing the suggestion. This comment could be a simple statement showing your approval (e.g. "I like this!") or it could also list additional use cases in which the proposed feature would be useful which were not mentioned elsewhere. This helps gauge the relative support for suggestions and ammendments.

## Want to Make a Direct Contribution?

Stensl welcomes all direct contributions whether they are to patch a bug, add a new feature, improve performance, or otherwise. If you want to help add to Stensl, you're welcome to read through the list of issues and suggestions to find a particular issue to address. If you'd like to add a feature or fix a bug not listed already, please file the proper issue first and then continue. Otherwise, once you find something you'd like to work on, you can fork Stensl's repository, implememt your changes, and then submit a pull request. In the pull request, you should have the following:

- What version of the Stensl repo you forked originally
- An explanation of what feature you've added or what bug you've patched. Explain it in your own words and attach a link to the issue which your additions address. 
- Some simple tests of the output produced by your new addition and under what circumstances they were produced.
- A verification that you've run the [demo programs](https://github.com/jtint24/Stensl-Demo-Programs) to help verify that you haven't introduced breaking changes. If the changes you've added do break backwards compatibility, then please explain why, detail exactly what situations will have broken backwards compatibility, and add some other verification that you haven't introduced breaking changes beyond the scope of what you've intended, such as your own demo programs. Additional verification will likely be performed, but this also helps you ensure that your code has no obvious breaking changes.


