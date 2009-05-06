package com.sd_editions.collatex.permutations;

import java.util.HashMap;

import junit.framework.TestCase;

import org.junit.Test;

public class CollateCoreTest extends TestCase {
  @Test
  public void testDetermineBase() {
    CollateCore collateCore = new CollateCore("The Black Cat", "The Cat and the Dog", "The White Cat");
    String witnessId1 = collateCore.getWitness(1).id;
    String witnessId2 = collateCore.getWitness(2).id;
    String witnessId3 = collateCore.getWitness(3).id;
    HashMap<String, MultiMatch> generatedBase = collateCore.generateBase();
    assertEquals(2, generatedBase.keySet().size());
    assertTrue(generatedBase.containsKey("the"));
    assertTrue(generatedBase.containsKey("cat"));

    MultiMatch theMultiMatch = generatedBase.get("the");
    // 'the' occurs once in the 1st witness
    assertEquals(1, theMultiMatch.getOccurancesInWitness(witnessId1).size());
    // 'the' occurs twice in the 2nd witness
    assertEquals(2, theMultiMatch.getOccurancesInWitness(witnessId2).size());
    // 'the' occurs once in the 3rd witness
    assertEquals(1, theMultiMatch.getOccurancesInWitness(witnessId3).size());
  }

  @Test
  public void testMultiMatchShrinksAtThirdWitness() {
    CollateCore collateCore = new CollateCore("The Black Cat", "The black dog and white cat", "The White Cat");
    String witnessId1 = collateCore.getWitness(1).id;
    String witnessId2 = collateCore.getWitness(2).id;
    String witnessId3 = collateCore.getWitness(3).id;
    HashMap<String, MultiMatch> generatedBase = collateCore.generateBase();
    assertEquals(2, generatedBase.keySet().size());
    assertTrue(generatedBase.containsKey("the"));
    assertTrue(generatedBase.containsKey("cat"));

    MultiMatch theMultiMatch = generatedBase.get("the");
    assertEquals(1, theMultiMatch.getOccurancesInWitness(witnessId1).size());
    assertEquals(1, theMultiMatch.getOccurancesInWitness(witnessId2).size());
    assertEquals(1, theMultiMatch.getOccurancesInWitness(witnessId3).size());
  }

}