
import com.campspot.dao.JokeDAO
import com.campspot.jdbi3.DAOManager

class Service(
    private val helloWorldConfiguration: HelloWorldConfiguration,
    private val daoManager: DAOManager
) {

    fun takeString(str: String?): String {
        var res = if (str != null) str else helloWorldConfiguration.defaultString
        return res
    }

    fun createJoke(jokeType: String?, jokeSetup: String?, jokePunchLine: String?): Joke {
        var joke: Joke
        if (jokeType != null && jokeSetup != null && jokePunchLine != null) {
            joke = Joke(
                type = jokeType,
                setup = jokeSetup,
                punchline = jokePunchLine
            )
            daoManager[JokeDAO::class].persist(joke)
            return joke
        }
        else {
            return Joke(
                type = "general",
                setup = "Error",
                punchline = "Joke"
            )
        }
    }
}