import org.scalatest.{Matchers, WordSpec}

/**
  * Created by ihor_mielientiev on 4/26/16.
  */
class TestSpec extends WordSpec with Matchers {

  "This test" should {
    "passs" in {
      1 + 10 shouldEqual 11
    }
  }
}
