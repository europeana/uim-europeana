/*
 * Copyright 2007-2015 The Europeana Foundation
 * 
 * Licensed under the EUPL, Version 1.1 (the "License") and subsequent versions as approved by the
 * European Commission; You may not use this work except in compliance with the License.
 * 
 * You may obtain a copy of the License at: http://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" basis, without warranties or conditions of any kind, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package eu.europeana.uim.repoxclient.rest;

import java.util.List;

import javax.ws.rs.InternalServerErrorException;

import pt.utl.ist.dataProvider.Aggregator;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;
import eu.europeana.uim.repox.model.RepoxConnectionStatus;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Apr 29, 2015
 */
public interface RepoxUIMServiceT {
  
  /**
   * Get the connection status URL
   * @return RepoxConnectionStatus
   */
  RepoxConnectionStatus showConnectionStatus();

  /******************** Aggregator Calls ********************/
  /**
   * Create an aggregator.
   * 
   * @param id
   * @param name
   * @param nameCode
   * @param homepage
   * @throws InvalidArgumentsException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   * @throws InternalServerErrorException
   */
  void createAggregator(String id, String name, String nameCode, String homepage)
      throws InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException,
      InternalServerErrorException;

  /**
   * Delete an aggregator by specifying the Id.
   * 
   * @param aggregatorId
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  void deleteAggregator(String aggregatorId) throws DoesNotExistException,
      InternalServerErrorException;

  /**
   * Retrieve the aggregator with the provided id.
   * 
   * @param aggregatorId
   * @return boolean value
   */
  boolean aggregatorExists(String aggregatorId);

  /**
   * Update an aggregator by specifying the Id. Aggregator newId can be null if there is no need to
   * change the id.
   * 
   * @param id
   * @param newId
   * @param name
   * @param nameCode
   * @param homepage
   * @throws InvalidArgumentsException
   * @throws MissingArgumentsException
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  void updateAggregator(String id, String newId, String name, String nameCode, String homepage)
      throws InvalidArgumentsException, MissingArgumentsException, DoesNotExistException,
      InternalServerErrorException;
  
  /**
   * Get a list of aggregators in the specified range. Returned number can be smaller than the
   * requested. Offset not allowed negative. If number is negative then it returns all the items
   * from offset until the total number of items.
   * 
   * @param offset
   * @param number
   * @return
   * @throws InvalidArgumentsException
   */
  List<Aggregator> getAggregatorList(int offset, int number)throws InvalidArgumentsException;

}
