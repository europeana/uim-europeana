package eu.europeana.uim.plugin.solr.utils;

import eu.europeana.corelib.definitions.jibx.AgentType;
import eu.europeana.corelib.definitions.jibx.AltLabel;
import eu.europeana.corelib.definitions.jibx.Concept;
import eu.europeana.corelib.definitions.jibx.HasMet;
import eu.europeana.corelib.definitions.jibx.HasPart;
import eu.europeana.corelib.definitions.jibx.Identifier;
import eu.europeana.corelib.definitions.jibx.IsPartOf;
import eu.europeana.corelib.definitions.jibx.IsRelatedTo;
import eu.europeana.corelib.definitions.jibx.Name;
import eu.europeana.corelib.definitions.jibx.Note;
import eu.europeana.corelib.definitions.jibx.PlaceType;
import eu.europeana.corelib.definitions.jibx.PrefLabel;
import eu.europeana.corelib.definitions.jibx.SameAs;
import eu.europeana.corelib.definitions.jibx.TimeSpanType;

public class JibxUtils {

	public AgentType mergeAgentFields(AgentType fAgent, AgentType sAgent) {
		AgentType agent = new AgentType();
		agent.setAbout(fAgent.getAbout().length() < sAgent.getAbout().length() ? fAgent
				.getAbout() : sAgent.getAbout());
		if (fAgent.getBegin() != null || sAgent.getBegin() != null) {
			agent.setBegin(fAgent.getBegin() != null ? fAgent.getBegin()
					: sAgent.getBegin());
		}
		if (fAgent.getBiographicalInformation() != null
				|| sAgent.getBiographicalInformation() != null) {
			agent.setBiographicalInformation(fAgent
					.getBiographicalInformation() != null ? fAgent
					.getBiographicalInformation() : sAgent
					.getBiographicalInformation());
		}
		if (fAgent.getDateOfBirth() != null || sAgent.getDateOfBirth() != null) {
			agent.setDateOfBirth(fAgent.getDateOfBirth() != null ? fAgent
					.getDateOfBirth() : sAgent.getDateOfBirth());
		}
		if (fAgent.getDateOfDeath() != null || sAgent.getDateOfDeath() != null) {
			agent.setDateOfDeath(fAgent.getDateOfDeath() != null ? fAgent
					.getDateOfDeath() : sAgent.getDateOfDeath());
		}
		if (fAgent.getDateOfEstablishment() != null
				|| sAgent.getDateOfEstablishment() != null) {
			agent.setDateOfEstablishment(fAgent.getDateOfEstablishment() != null ? fAgent
					.getDateOfEstablishment() : sAgent.getDateOfEstablishment());
		}
		if (fAgent.getDateOfTermination() != null
				|| sAgent.getDateOfTermination() != null) {
			agent.setDateOfTermination(fAgent.getDateOfTermination() != null ? fAgent
					.getDateOfTermination() : sAgent.getDateOfTermination());
		}
		if (fAgent.getEnd() != null || sAgent.getEnd() != null) {
			agent.setEnd(fAgent.getEnd() != null ? fAgent.getEnd() : sAgent
					.getEnd());
		}
		if (fAgent.getGender() != null || sAgent.getGender() != null) {
			agent.setGender(fAgent.getGender() != null ? fAgent.getGender()
					: sAgent.getGender());
		}
		if (fAgent.getProfessionOrOccupation() != null
				|| sAgent.getPrefLabelList() != null) {
			agent.setProfessionOrOccupation(fAgent.getProfessionOrOccupation() != null ? fAgent
					.getProfessionOrOccupation() : sAgent
					.getProfessionOrOccupation());
		}
		if (sAgent.getPrefLabelList() != null) {
			agent.setPrefLabelList(sAgent.getPrefLabelList());
			if (fAgent.getPrefLabelList() != null) {

				for (PrefLabel prefLabel : fAgent.getPrefLabelList()) {
					if (!agent.getPrefLabelList().contains(prefLabel)) {
						agent.getPrefLabelList().add(prefLabel);
					}
				}
			}

		} else {
			if (fAgent.getPrefLabelList() != null) {
				agent.setPrefLabelList(fAgent.getPrefLabelList());
			}
		}

		if (sAgent.getAltLabelList() != null) {
			agent.setAltLabelList(sAgent.getAltLabelList());
			if (fAgent.getAltLabelList() != null) {

				for (AltLabel altLabel : fAgent.getAltLabelList()) {
					if (!agent.getAltLabelList().contains(altLabel)) {
						agent.getAltLabelList().add(altLabel);
					}
				}
			}

		} else {
			if (fAgent.getAltLabelList() != null) {
				agent.setAltLabelList(fAgent.getAltLabelList());
			}
		}

		if (sAgent.getDateList() != null) {
			agent.setDateList(sAgent.getDateList());
			if (fAgent.getDateList() != null) {

				for (eu.europeana.corelib.definitions.jibx.Date date : fAgent
						.getDateList()) {
					if (!agent.getDateList().contains(date)) {
						agent.getDateList().add(date);
					}
				}

			}
		} else {
			if (fAgent.getDateList() != null) {
				agent.setDateList(fAgent.getDateList());
			}
		}

		if (sAgent.getHasMetList() != null) {
			agent.setHasMetList(sAgent.getHasMetList());
			if (fAgent.getHasMetList() != null) {

				for (HasMet hasMet : fAgent.getHasMetList()) {
					if (!agent.getHasMetList().contains(hasMet)) {
						agent.getHasMetList().add(hasMet);
					}
				}
			}

		} else {
			if (fAgent.getHasMetList() != null) {
				agent.setHasMetList(fAgent.getHasMetList());
			}
		}

		if (sAgent.getIdentifierList() != null) {
			agent.setIdentifierList(sAgent.getIdentifierList());
			if (fAgent.getIdentifierList() != null) {

				for (Identifier identifier : fAgent.getIdentifierList()) {
					if (!agent.getIdentifierList().contains(identifier)) {
						agent.getIdentifierList().add(identifier);
					}
				}
			}

		} else {
			if (fAgent.getIdentifierList() != null) {
				agent.setIdentifierList(fAgent.getIdentifierList());
			}
		}

		if (sAgent.getIsRelatedToList() != null) {
			agent.setIsRelatedToList(sAgent.getIsRelatedToList());
			if (fAgent.getIsRelatedToList() != null) {
				for (IsRelatedTo isRelatedTo : fAgent.getIsRelatedToList()) {
					if (!agent.getIsRelatedToList().contains(isRelatedTo)) {
						agent.getIsRelatedToList().add(isRelatedTo);
					}
				}
			}
		} else {
			if (fAgent.getIsRelatedToList() != null) {
				agent.setIsRelatedToList(fAgent.getIsRelatedToList());
			}
		}

		if (sAgent.getNoteList() != null) {
			agent.setNoteList(sAgent.getNoteList());
			if (fAgent.getNoteList() != null) {
				for (Note note : fAgent.getNoteList()) {
					if (!agent.getNoteList().contains(note)) {
						agent.getNoteList().add(note);
					}
				}
			}
		} else {
			if (fAgent.getNoteList() != null) {
				agent.setNoteList(fAgent.getNoteList());
			}
		}

		if (sAgent.getNameList() != null) {
			agent.setNameList(sAgent.getNameList());
			if (fAgent.getNameList() != null) {
				for (Name name : fAgent.getNameList()) {
					if (!agent.getNameList().contains(name)) {
						agent.getNameList().add(name);
					}
				}
			}
		} else {
			if (fAgent.getNameList() != null) {
				agent.setNameList(fAgent.getNameList());
			}
		}

		if (sAgent.getSameAList() != null) {
			agent.setSameAList(sAgent.getSameAList());
			if (fAgent.getSameAList() != null) {
				for (SameAs sameAs : fAgent.getSameAList()) {
					if (!agent.getSameAList().contains(sameAs)) {
						agent.getSameAList().add(sameAs);
					}
				}
			}
		} else {
			if (fAgent.getSameAList() != null) {
				agent.setSameAList(fAgent.getSameAList());
			}
		}
		return agent;
	}

	public Concept mergeConceptsField(Concept fConcept, Concept sConcept) {
		Concept concept = new Concept();
		concept.setAbout(fConcept.getAbout().length() < sConcept.getAbout()
				.length() ? fConcept.getAbout() : sConcept.getAbout());
		concept.setChoiceList(sConcept.getChoiceList());
		for (Concept.Choice choice : fConcept.getChoiceList()) {
			if (!concept.getChoiceList().contains(choice)) {
				concept.getChoiceList().add(choice);
			}
		}
		return concept;
	}

	public TimeSpanType mergeTimespanFields(TimeSpanType fTs, TimeSpanType sTs) {
		TimeSpanType ts = new TimeSpanType();
		ts.setAbout(fTs.getAbout().length() < sTs.getAbout().length() ? fTs
				.getAbout() : sTs.getAbout());
		if (fTs.getBegin() != null || sTs.getBegin() != null) {
			ts.setBegin(fTs.getBegin() != null ? fTs.getBegin() : sTs
					.getBegin());
		}
		if (fTs.getEnd() != null || sTs.getEnd() != null) {
			ts.setEnd(fTs.getEnd() != null ? fTs.getEnd() : sTs.getEnd());
		}
		
		if (sTs.getAltLabelList() != null) {
			ts.setAltLabelList(sTs.getAltLabelList());
			if (fTs.getAltLabelList() != null) {
					for (AltLabel altLabel : fTs.getAltLabelList()) {
						if (!ts.getAltLabelList().contains(altLabel)) {
							ts.getAltLabelList().add(altLabel);
						}
					}
				}
		} else {
			if (fTs.getAltLabelList() != null) {
				fTs.setAltLabelList(fTs.getAltLabelList());
			}
		}
		
		
		
		if (sTs.getPrefLabelList() != null) {
			ts.setPrefLabelList(sTs.getPrefLabelList());
			if (fTs.getPrefLabelList() != null) {
					for (PrefLabel prefLabel : fTs.getPrefLabelList()) {
						if (!ts.getPrefLabelList().contains(prefLabel)) {
							ts.getPrefLabelList().add(prefLabel);
						}
					}
				}
		} else {
			if (fTs.getPrefLabelList() != null) {
				ts.setPrefLabelList(fTs.getPrefLabelList());
			}
		}
		
		if (sTs.getHasPartList() != null) {
			ts.setHasPartList(sTs.getHasPartList());
			if (fTs.getHasPartList() != null) {
					for (HasPart hasPart: fTs.getHasPartList()) {
						if (!ts.getHasPartList().contains(hasPart)) {
							ts.getHasPartList().add(hasPart);
						}
					}
				}
		} else {
			if (fTs.getHasPartList() != null) {
				ts.setHasPartList(fTs.getHasPartList());
			}
		}
		
		if (sTs.getIsPartOfList() != null) {
			ts.setIsPartOfList(sTs.getIsPartOfList());
			if (fTs.getIsPartOfList() != null) {
					for (IsPartOf isPartOf: fTs.getIsPartOfList()) {
						if (!ts.getIsPartOfList().contains(isPartOf)) {
							ts.getIsPartOfList().add(isPartOf);
						}
					}
				}
		} else {
			if (fTs.getIsPartOfList() != null) {
				ts.setIsPartOfList(fTs.getIsPartOfList());
			}
		}
		
		if (sTs.getNoteList() != null) {
			ts.setNoteList(sTs.getNoteList());
			if (fTs.getNoteList() != null) {
					for (Note note: fTs.getNoteList()) {
						if (!ts.getNoteList().contains(note)) {
							ts.getNoteList().add(note);
						}
					}
				}
		} else {
			if (fTs.getNoteList() != null) {
				ts.setNoteList(fTs.getNoteList());
			}
		}
		
		if (sTs.getSameAList() != null) {
			ts.setSameAList(sTs.getSameAList());
			if (fTs.getSameAList() != null) {
					for (SameAs sameAs: fTs.getSameAList()) {
						if (!ts.getSameAList().contains(sameAs)) {
							ts.getSameAList().add(sameAs);
						}
					}
				}
		} else {
			if (fTs.getSameAList() != null) {
				ts.setSameAList(fTs.getSameAList());
			}
		}
		
		return ts;
	}

	public PlaceType mergePlacesFields(PlaceType fPlace, PlaceType sPlace) {
		PlaceType place = new PlaceType();
		place.setAbout(fPlace.getAbout().length()<sPlace.getAbout().length()?fPlace.getAbout():sPlace.getAbout());
		
		
		if (sPlace.getAltLabelList() != null) {
			place.setAltLabelList(sPlace.getAltLabelList());
			if (fPlace.getAltLabelList() != null) {
					for (AltLabel altLabel : fPlace.getAltLabelList()) {
						if (!place.getAltLabelList().contains(altLabel)) {
							place.getAltLabelList().add(altLabel);
						}
					}
				}
		} else {
			if (fPlace.getAltLabelList() != null) {
				fPlace.setAltLabelList(fPlace.getAltLabelList());
			}
		}
		
		
		
		if (sPlace.getPrefLabelList() != null) {
			place.setPrefLabelList(sPlace.getPrefLabelList());
			if (fPlace.getPrefLabelList() != null) {
					for (PrefLabel prefLabel : fPlace.getPrefLabelList()) {
						if (!place.getPrefLabelList().contains(prefLabel)) {
							place.getPrefLabelList().add(prefLabel);
						}
					}
				}
		} else {
			if (fPlace.getPrefLabelList() != null) {
				place.setPrefLabelList(fPlace.getPrefLabelList());
			}
		}
		
		if (sPlace.getHasPartList() != null) {
			place.setHasPartList(sPlace.getHasPartList());
			if (fPlace.getHasPartList() != null) {
					for (HasPart hasPart: fPlace.getHasPartList()) {
						if (!place.getHasPartList().contains(hasPart)) {
							place.getHasPartList().add(hasPart);
						}
					}
				}
		} else {
			if (fPlace.getHasPartList() != null) {
				place.setHasPartList(fPlace.getHasPartList());
			}
		}
		
		if (sPlace.getIsPartOfList() != null) {
			place.setIsPartOfList(sPlace.getIsPartOfList());
			if (fPlace.getIsPartOfList() != null) {
					for (IsPartOf isPartOf: fPlace.getIsPartOfList()) {
						if (!place.getIsPartOfList().contains(isPartOf)) {
							place.getIsPartOfList().add(isPartOf);
						}
					}
				}
		} else {
			if (fPlace.getIsPartOfList() != null) {
				place.setIsPartOfList(fPlace.getIsPartOfList());
			}
		}
		
		if (sPlace.getNoteList() != null) {
			place.setNoteList(sPlace.getNoteList());
			if (fPlace.getNoteList() != null) {
					for (Note note: fPlace.getNoteList()) {
						if (!place.getNoteList().contains(note)) {
							place.getNoteList().add(note);
						}
					}
				}
		} else {
			if (fPlace.getNoteList() != null) {
				place.setNoteList(fPlace.getNoteList());
			}
		}
		
		if (sPlace.getSameAList() != null) {
			place.setSameAList(sPlace.getSameAList());
			if (fPlace.getSameAList() != null) {
					for (SameAs sameAs: fPlace.getSameAList()) {
						if (!place.getSameAList().contains(sameAs)) {
							place.getSameAList().add(sameAs);
						}
					}
				}
		} else {
			if (fPlace.getSameAList() != null) {
				place.setSameAList(fPlace.getSameAList());
			}
		}
		
		if(fPlace.getLat()!=null||sPlace.getLat()!=null){
			place.setLat(fPlace.getLat()!=null?fPlace.getLat():sPlace.getLat());
		}
		
		if(fPlace.getLong()!=null||sPlace.getLong()!=null){
			place.setLong(fPlace.getLong()!=null?fPlace.getLong():sPlace.getLong());
		}
		
		if(fPlace.getAlt()!=null||sPlace.getAlt()!=null){
			place.setAlt(fPlace.getAlt()!=null?fPlace.getAlt():sPlace.getAlt());
		}
		return place;
	}

	
	
}
