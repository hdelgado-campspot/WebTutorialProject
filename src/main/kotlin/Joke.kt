

import com.campspot.dao.entities.CampspotModel
import lombok.Getter
import lombok.Setter
import javax.persistence.*

//@Table(name = "joke")
//@Entity
//@Getter
//@Setter
data class Joke(

    val id: Int? = null,

    val type: String,

    val setup: String,

    val punchline: String? = null
){

}
