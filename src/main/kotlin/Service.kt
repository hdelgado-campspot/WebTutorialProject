class Service(
    private val helloWorldConfiguration: HelloWorldConfiguration
) {
    private var id: Int = 5
    fun takeString(str: String?): String {
        var res = if (str != null) str else helloWorldConfiguration.defaultString
        return res
    }

    fun createJoke(jokeType: String?, jokeSetup: String?, jokePunchLine: String?): Joke {
        var joke: Joke
        if (jokeType != null && jokeSetup != null && jokePunchLine != null) {
            joke = Joke(id, jokeType, jokeSetup, jokePunchLine)
            return joke
        }
        else {
            return Joke(id, "general", "Error", "Joke")
        }
    }
}