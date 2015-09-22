import unittest
from collatex import Collation
from collatex.core_classes import VariantGraph
from collatex.edit_graph_aligner import EditGraphAligner

__author__ = 'ronalddekker'




class NewScorer(object):
    def __init__(self, lcp_intervals, collation):
        self.lcp_intervals = lcp_intervals
        self.collation = collation
        self.global_tokens_to_lcp_intervals = {}

    def prepare_witness(self, witness):
        lcp_intervals = self._build_token_to_lcp_interval(witness)
        self.global_tokens_to_lcp_intervals.update(lcp_intervals)
        pass

    def score_cell(self, cell, parent_node, token_a, token_b, y, x, edit_operation):
        interval_a = self.global_tokens_to_lcp_intervals[token_a]
        interval_b = self.global_tokens_to_lcp_intervals[token_b]
        print(interval_a)
        print(interval_b)
        # do the matching between the two intervals.
        # note: one of them can be None
        if interval_a == interval_b:
            print("MATCH")
        # If we want to doing the scoring right
        # We need to look whether to matching interval


        raise Exception("not implemented!")
        pass


    # #TODO: it should be possible to do this simpler, faster
    # # An occurrence should know its tokens, since it knows its token range
    # def _build_tokens_to_occurrences(self, collation, witness, block_witness):
    #     tokens_to_occurrence = {}
    #     witness_range = collation.get_range_for_witness(witness.sigil)
    #     token_counter = witness_range[0]
    #     # note: this can be done faster by focusing on the occurrences
    #     # instead of the tokens
    #     for token in witness.tokens():
    #         for occurrence in block_witness.occurrences:
    #             if occurrence.is_in_range(token_counter):
    #                 tokens_to_occurrence[token]=occurrence
    #         token_counter += 1
    #     return tokens_to_occurrence

    # note: each token can be associated with multiple lcp intervals
    # note; this method has many for loops; it must be possible to do this in a faster way
    def _build_token_to_lcp_interval(self, witness):
        tokens_to_interval = {}
        witness_range = self.collation.get_range_for_witness(witness.sigil)
        token_counter = witness_range[0]
        for token in witness.tokens():
            for lcp_interval in self.lcp_intervals:
                for occurrence in lcp_interval.block_occurrences():
                    if occurrence == token_counter:
                        # Note: there can be multiple intervals for the same token!
                        # this should be handled correctly
                        tokens_to_interval[token]=lcp_interval
        return tokens_to_interval





        pass


class Test(unittest.TestCase):
    def test_new_scorer(self):
        collation = Collation()
        collation.add_plain_witness("A", "a b c")
        collation.add_plain_witness("B", "a b c d")
        # prepare index
        # the scorer later on uses the index to make scoring decisions
        extended_suffix_array = collation.to_extended_suffix_array()
        print(extended_suffix_array)
        lcp_intervals = extended_suffix_array.split_lcp_array_into_intervals()
        for lcp_interval in lcp_intervals:
            print(lcp_interval)
        # we pass the lcp_intervals to the scorer
        graph = VariantGraph()
        scorer = NewScorer(lcp_intervals, collation)
        aligner = EditGraphAligner(collation, scorer)
        aligner.collate(graph, collation)
