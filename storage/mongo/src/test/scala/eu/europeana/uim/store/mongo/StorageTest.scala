package eu.europeana.uim.store.mongo

import com.mongodb.Mongo
import org.junit.Test
import org.scalatest.junit.{ShouldMatchersForJUnit, JUnitSuite}
import eu.europeana.uim.{TKey, MDRFieldRegistry, MetaDataRecord}
import java.util.ArrayList
import eu.europeana.uim.api.StorageEngineException
import eu.europeana.uim.store.Execution

/**
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */

class StorageTest extends JUnitSuite with ShouldMatchersForJUnit {



  def withEngine(testFunction: MongoStorageEngine => Unit) {
    val e: MongoStorageEngine = new MongoStorageEngine("UIMTEST")
    e.initialize

    try {
      testFunction(e)
    } finally {
      e.shutdown
      // clear everything
      val m: Mongo = new Mongo();
      m.dropDatabase("UIMTEST");
    }
  }

  // providers

  /**
   * engine creates providers and returns an incremented id
   */
  @Test def createProvider() {
    withEngine{
      engine => {
        val p = engine.createProvider
        val p1 = engine.createProvider
        p.getId should equal(0)
        p1.getId should equal(1)
      }
    }
  }

  /**
   * engine updates providers properly
   */
  @Test def updateProvider() {
    withEngine{
      engine => {
        val p = engine.createProvider
        p.getName should equal(null)
        p.setName("MyLibrary")
        p.setMnemonic("1")
        engine.updateProvider(p)

        val p1 = engine.getProvider(p.getId)
        p.getName should equal("MyLibrary")

        val p2 = engine.createProvider
        p2.setName("MyLibrary")

        try {
          engine.updateProvider(p2)
          fail()
        } catch {
          case e: StorageEngineException => //Expected
        }

        p2.setName("MyOtherLibrary")
        p2.setMnemonic("1")

        try {
          engine.updateProvider(p2)
          fail()
        } catch {
          case e: StorageEngineException => //Expected
        }

      }
    }
  }

  /**
   * engine retrieves providers properly
   */
  @Test def retrieveProvider() {
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

  /**
   * engine creates collections and returns an incremented id
   */
  @Test def createCollection() {
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

  /**
   * engine updates collections properly
   */
  @Test def updateCollection() {
    withEngine{
      engine => {
        val p = engine.createProvider
        val c = engine.createCollection(p)
        c.getName should equal(null)
        c.setName("MyCollection")
        c.setMnemonic("1")
        engine.updateCollection(c)

        val c1 = engine.getCollection(c.getId)
        c1.getName should equal("MyCollection")

        val c2 = engine.createCollection(p)
        c2.setName("MyCollection")

        try {
          engine.updateCollection(c2)
          fail()
        } catch {
          case e: StorageEngineException => //Expected
        }

        c2.setName("MyOtherCollection")
        c2.setMnemonic("1")

        try {
          engine.updateCollection(c2)
          fail()
        } catch {
          case e: StorageEngineException => //Expected
        }

      }
    }
  }

  /**
   * engine retrieves collections properly
   */
  @Test def retrieveCollection() {
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

  /**
   * engine creates requests and returns an incremented id
   */
  @Test def createRequest() {
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

  /**
   * engine retrieves requests properly
   */
  @Test def retrieveRequest() {
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

  /**
   * engine creates executions and returns an incremented id
   */
  @Test def createExecution() {
    withEngine{
      engine => {
        val e = engine.createExecution
        val e1 = engine.createExecution
        e.getId should equal(0)
        e1.getId should equal(1)
      }
    }
  }

  /**
   * engine retrieves executions properly
   */
  @Test def retrieveExecution() {
    withEngine{
      engine =>
        val e = engine.createExecution
        val id = e.getId
        engine.getExecutions().size should equal(1)
        val e1:Execution = engine.getExecutions().get(0)
        e1.isActive should equal (false)
    }
  }

  // mdrs

  /**
   * engine creates mdrs and returns an incremented id
   */
  @Test def createMdr() {
    withEngine{
      engine => {
        val p = engine.createProvider()
        val c = engine.createCollection(p)
        val r = engine.createRequest(c)
        val mdr = engine.createMetaDataRecord(r)
        val mdr1 = engine.createMetaDataRecord(r)
        mdr.getId should equal(0)
        mdr1.getId should equal(1)
      }
    }
  }


  /**
   * engine retrieves lists of mdrs
   */
  @Test def retrieveMdrList() {
    withEngine{
      engine => {
        val p = engine.createProvider()
        val c = engine.createCollection(p)
        val r = engine.createRequest(c)
        val mdr = engine.createMetaDataRecord(r)
        val mdr1 = engine.createMetaDataRecord(r)
        val mdr2 = engine.createMetaDataRecord(r)

        val l: Array[MetaDataRecord[MDRFieldRegistry]] = engine.getMetaDataRecords(0, 1, 2)
        l(0).getRequest.getId should equal(r.getId)
        l(1).getRequest.getId should equal(r.getId)
        l(2).getRequest.getId should equal(r.getId)
      }
    }
  }

  /**
   * engine retrieves mdrs by request
   */
  @Test def retrieveAndCountMdrByRequest() {
    withEngine{
      engine => {
        val p = engine.createProvider()
        val c = engine.createCollection(p)
        val r = engine.createRequest(c)
        val mdr = engine.createMetaDataRecord(r)
        val mdr1 = engine.createMetaDataRecord(r)
        val mdr2 = engine.createMetaDataRecord(r)

        val l: Array[Long] = engine.getByRequest(r)
        l.length should equal(3)

        engine.getTotalByRequest(r) should equal(3)
      }
    }
  }

  /**
   * engine retrieves mdrs by collection
   */
  @Test def retrieveAndCountMdrByCollection() {
    withEngine{
      engine => {
        val p = engine.createProvider()
        val c = engine.createCollection(p)
        val r = engine.createRequest(c)
        val r1 = engine.createRequest(c)
        val mdr1 = engine.createMetaDataRecord(r)
        val mdr2 = engine.createMetaDataRecord(r)
        val mdr3 = engine.createMetaDataRecord(r1)
        val mdr4 = engine.createMetaDataRecord(r1)

        val l: Array[Long] = engine.getByCollection(c)
        l.length should equal(4)

        engine.getTotalByCollection(c) should equal(4)
      }
    }
  }

  /**
   * engine retrieves mdrs by provider
   */
  @Test def retrieveAndCountMdrByProvider() {
    withEngine{
      engine => {
        val p = engine.createProvider()
        val c = engine.createCollection(p)
        val c1 = engine.createCollection(p)
        val r = engine.createRequest(c)
        val r1 = engine.createRequest(c1)
        val mdr1 = engine.createMetaDataRecord(r)
        val mdr2 = engine.createMetaDataRecord(r)
        val mdr3 = engine.createMetaDataRecord(r1)
        val mdr4 = engine.createMetaDataRecord(r1)

        val l: Array[Long] = engine.getByProvider(p, true)
        l.length should equal(4)

        engine.getTotalByProvider(p, true) should equal(4)
      }
    }
  }

  /**
   * engine retrieves tutti mdrs
   */
  @Test def retrieveAndCountTutti() {
    withEngine{
      engine => {
        val p = engine.createProvider()
        val c = engine.createCollection(p)
        val c1 = engine.createCollection(p)
        val c2 = engine.createCollection(p)
        val c3 = engine.createCollection(p)
        val r = engine.createRequest(c)
        val r1 = engine.createRequest(c1)
        val r2 = engine.createRequest(c2)
        val r3 = engine.createRequest(c3)
        val mdr1 = engine.createMetaDataRecord(r)
        val mdr2 = engine.createMetaDataRecord(r1)
        val mdr3 = engine.createMetaDataRecord(r2)
        val mdr4 = engine.createMetaDataRecord(r3)
        val mdr5 = engine.createMetaDataRecord(r3)

        val l: Array[Long] = engine.getByProvider(p, true)
        l.length should equal(5)

        engine.getTotalForAllIds() should equal(5)
      }
    }
  }

  val multiValueTitle: TKey[MDRFieldRegistry, ArrayList[String]] = TKey.register(classOf[MDRFieldRegistry], "multiValueTitle", classOf[ArrayList[String]])
  val multiValueAuthor: TKey[MDRFieldRegistry, ArrayList[String]] = TKey.register(classOf[MDRFieldRegistry], "multiValueTitle", classOf[ArrayList[String]])

  @Test def mdrTest() {
    withEngine{
      engine => {
        val p = engine.createProvider()
        val c = engine.createCollection(p)
        val r = engine.createRequest(c)
        val mdr: MetaDataRecord[MDRFieldRegistry] = engine.createMetaDataRecord(r)

        //unqualified
        mdr.addField(multiValueAuthor, "Goethe");
        mdr.addField(multiValueAuthor, "Schiller");

        //qualified
        mdr.addQField(multiValueTitle, "lang:en", "Subtitle")
        mdr.addQField(multiValueTitle, "lang:en", "Subtitle 2")
        mdr.addQField(multiValueTitle, "lang:fr", "Soustitre")

        engine.updateMetaDataRecord(mdr)

        val mdr1: MetaDataRecord[MDRFieldRegistry] = engine.getMetaDataRecords(mdr.getId)(0)

        val a: java.util.List[String] = mdr1.getField(multiValueAuthor);
        a.size should equal(2)
        a.get(0) should equal("Goethe")
        a.get(1) should equal("Schiller")

        val l1: java.util.List[String] = mdr1.getQField(multiValueTitle, "lang:en")

        l1.size should equal(2)
        l1.get(0) should equal("Subtitle")
        l1.get(1) should equal("Subtitle 2")


        // first/principal field

        /*
        mdr1.setFirstField(multiValueAuthor, "Mephisto")
        mdr2.setQField(multiValueAuthor, "lang:en", "Faust")

        engine.updateMetaDataRecord(mdr1)

        val mdr2:MetaDataRecord[MDRFieldRegistry] = engine.getMetaDataRecords(mdr.getId)(0)

        mdr2.getField(multiValueAuthor).get(0) should equal ("Mephisto")
        mdr2.getQField(multiValueAuthor, "lang:en").get(0) should equal ("Faust")
        */
      }

    }
  }

}