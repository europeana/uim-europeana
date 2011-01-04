package eu.europeana.uim.store.mongo

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import com.mongodb.Mongo

/**
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */

class StorageTest extends FunSuite with ShouldMatchers {

  def withEngine(testFunction: MongoStorageEngine => Unit) {
    val e: MongoStorageEngine = new MongoStorageEngine("TEST")
    e.initialize

    try {
      testFunction(e)
    } finally {
      e.shutdown
      // clear everything
      val m: Mongo = new Mongo();
      m.dropDatabase("TEST");


    }
  }

  // providers

  test("engine creates providers and returns an incremented id") {
    withEngine{
      engine => {
        val p = engine.createProvider
        val p1 = engine.createProvider
        p.getId should equal(0)
        p1.getId should equal(1)
      }
    }
  }

  test("engine updates providers properly") {
    withEngine{
      engine => {
        val p = engine.createProvider
        p.getName should equal(null)
        p.setName("MyLibrary")
        engine.updateProvider(p)

        val p1 = engine.getProvider(p.getId)
        p.getName should equal("MyLibrary")
      }
    }
  }

  test("engine retrieves providers properly") {
    withEngine{
      engine =>
        val p = engine.createProvider
        val id = p.getId
        val pp = engine.getProvider(id)
        p should equal(pp)

        engine.getProvider.size should equal(1);
    }
  }




  // collections

  test("engine creates collections and returns an incremented id") {
    withEngine{
      engine => {
        val p = engine.createProvider
        val c = engine.createCollection(p)
        val c1 = engine.createCollection(p)
        c.getId should equal(0)
        c1.getId should equal(1)
      }
    }
  }

  test("engine updates collections properly") {
    withEngine{
      engine => {
        val p = engine.createProvider
        val c = engine.createCollection(p)
        c.getName should equal(null)
        c.setName("MyCollection")
        engine.updateCollection(c)

        val c1 = engine.getCollection(c.getId)
        c1.getName should equal("MyCollection")
      }
    }
  }

  test("engine retrieves collections properly") {
    withEngine{
      engine =>
        val p = engine.createProvider
        val c = engine.createCollection(p)
        val id = c.getId
        val cc = engine.getCollection(id)
        c should equal(cc)

        engine.getCollections(p).size should equal(1);
    }
  }

  // requests

  test("engine creates requests and returns an incremented id") {
    withEngine{
      engine => {
        val p = engine.createProvider
        val c = engine.createCollection(p)
        val r = engine.createRequest(c)
        val r1 = engine.createRequest(c)
        r.getId should equal(0)
        r1.getId should equal(1)
      }
    }
  }

  test("engine retrieves requests properly") {
    withEngine{
      engine =>
        val p = engine.createProvider
        val c = engine.createCollection(p)
        val r = engine.createRequest(c)
        val id = r.getId

        engine.getRequests(c).size should equal(1);
    }
  }

  // executions

test("engine creates executions and returns an incremented id") {
    withEngine{
      engine => {
        val e = engine.createExecution
        val e1 = engine.createExecution
        e.getId should equal(0)
        e1.getId should equal(1)
      }
    }
  }

  test("engine retrieves executions properly") {
    withEngine{
      engine =>
        val e = engine.createExecution
        val id = e.getId
        engine.getExecutions().size should equal (1)
    }
  }


}