# starlingbank-interview by Joao Sardinha

### Hello reviewer 👋

The project uses all the latest build versions so the only change required to run should be the bearer token at:

```
package com.john.data

object HttpClient {

    private const val TOKEN = ""

    ...
}
```

### Architecture

![Untitled(2)](https://user-images.githubusercontent.com/15181917/201811760-7dccd39d-a9f5-4590-b020-75807e8cc103.png)

Each component's responsibility follows a simple clean architecture + mvvm setup. Everything is part of a single "app" module because I didn't think it was complex enough that it needed to be split.

### Things I didn't do because of time constraints

- Error handling is practically non existant, it will only show a generic error message if at any points something breaks getting the transactions/roundups, in a real project the repository would return from each exposed method an Error object with multiple subtypes and error details to help the presentation layer display the approriate message and recovery method.
- Loading: there's no loading state while fetching the transactions and performing the roundup.
- DI: all objects are being created in the viewmodel factory so they are attatched to the activity's lifecycle, in a real project I'd implement a more robust solution (probably either hilt or koin)
- Caching in the data layer is currently just a local variable for simplicity, a real solution would be something like an sql database (room etc)
- The transactions service api supports rounding up transactions within a date range as that seemed like a necessary feature, but the actual implementation in the data layer is missing because it would need better caching
- Data layer tests and UI tests
- There are a few other things I left to do, which should all have a docstring explaining it nearby!

I noticed in the starling api docs there's a /round-up method, I wasn't sure if it was expected to use that but I included the rounding logic in the application instead, either way this was my first time actually using Flow a bit so I enjoyed working on this test project :) 
