class Service(
    private val helloWorldConfiguration: HelloWorldConfiguration
) {
    fun takeString(str: String?): String {
        var res = if (str != null) str else helloWorldConfiguration.defaultString
        return res
    }
}