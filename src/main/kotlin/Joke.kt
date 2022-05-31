import javax.persistence.*

@Table(name = "joke")
@Entity
class Joke(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Int,

    @Column(name = "type", nullable = false, length = 100)
    var type: String,

    @Column(name = "setup", nullable = false, length = 100)
    var setup: String,

    @Column(name = "punchline", nullable = true, length = 100)
    var punchline: String? = null
)