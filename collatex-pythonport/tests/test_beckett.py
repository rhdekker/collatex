'''
Created on Jun 6, 2014

@author: Ronald Haentjens Dekker
'''
import unittest
from collatex import Collation
from collatex.collatex_dekker_algorithm import collate


class Test(unittest.TestCase):

    #TODO: last one only works when transposition detection is added.
    def testBeckett(self):
        collation = Collation()
        collation.add_witness("1", "The same clock as when for example Magee once died.")
        collation.add_witness("2", "The same as when for example Magee once died.")
        table = collate(collation, output="novisualization")
        self.assertEquals(["The same", "clock", "as when for example Magee once died."], table.rows[0].to_list())
        self.assertEquals(["The same", "-", "as when for example Magee once died."], table.rows[1].to_list())
        
#         table.print_plain_text()
#         "The same as when for example McKee once died .",//
#         "The same as when among others Darly once died & left him.",//
#       #  "The same as when Darly among others once died and left him.");
        pass

# 
# if __name__ == "__main__":
#     #import sys;sys.argv = ['', 'Test.testName']
#     unittest.main()