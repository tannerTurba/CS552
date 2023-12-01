package types;

// Sentence ::= UnarySentence | BinarySentence
abstract public class Sentence {
    protected Symbol symbol;

    public Symbol getSymbol() {
        return symbol;
    }

    public String toString() {
        return symbol.toString();
    }

    public Sentence convertToCNF() {
        Sentence result = this;
        result = result.eliminateIff(this);
        result = result.eliminateIf(this);
        result = result.eliminateNot(this, false);
        result = result.applyDistributivity(this);
        if (result instanceof UnarySentence) {
            UnarySentence us = (UnarySentence)result;
            if (us.nestedSentence != null) {
                return us.nestedSentence;
            }
        }
        return result;
    }

    private Sentence eliminateIff(Sentence sentence) {
        if (sentence instanceof BinarySentence) {
            BinarySentence binarySentence = (BinarySentence)sentence;
            UnarySentence u1 = (UnarySentence)eliminateIff(binarySentence.getS1());
            UnarySentence u2 = (UnarySentence)eliminateIff(binarySentence.getS2());
            if (binarySentence.getConnective() == BinaryConnective.IFF) {
                BinarySentence s1 = new BinarySentence(u1, BinaryConnective.IF, u2);
                BinarySentence s2 = new BinarySentence(u2, BinaryConnective.IF, u1);
                UnarySentence left = new UnarySentence(s1);
                UnarySentence right = new UnarySentence(s2);
                return new BinarySentence(left, BinaryConnective.AND, right);
            }
            return new BinarySentence(u1, binarySentence.getConnective(), u2);
        }
        else if (sentence instanceof UnarySentence) {
            UnarySentence unary = (UnarySentence)sentence;
            if (unary.nestedSentence != null) {
                unary.setNestedSentence(eliminateIff(unary.nestedSentence));
                return unary;
            }
        }
        return sentence;
    }

    private Sentence eliminateIf(Sentence sentence) {
        if (sentence instanceof BinarySentence) {
            BinarySentence binarySentence = (BinarySentence)sentence;
            UnarySentence u1 = (UnarySentence)eliminateIf(binarySentence.getS1());
            UnarySentence right = (UnarySentence)eliminateIf(binarySentence.getS2());
            BinaryConnective connective = binarySentence.getConnective();
            if (binarySentence.getConnective() == BinaryConnective.IF) {
                UnarySentence left = new UnarySentence(u1);
                return new BinarySentence(left, BinaryConnective.OR, right);
            }
            return new BinarySentence(u1, connective, right);
        }
        else if (sentence instanceof UnarySentence) {
            UnarySentence unary = (UnarySentence)sentence;
            if (unary.nestedSentence != null) {
                unary.setNestedSentence(eliminateIf(unary.nestedSentence));
                return unary;
            }
        }
        return sentence;
    }

    private Sentence eliminateNot(Sentence sentence, boolean outterIsNegated) {
        if (sentence instanceof BinarySentence) {
            BinarySentence binarySentence = (BinarySentence)sentence;
            boolean leftIsNegated = binarySentence.getS1().isNegated;
            boolean rightIsNegated = binarySentence.getS2().isNegated;
            if (outterIsNegated) {
                binarySentence = binarySentence.negate();
            }

            UnarySentence left = (UnarySentence)eliminateNot(binarySentence.getS1(), leftIsNegated);
            UnarySentence right = (UnarySentence)eliminateNot(binarySentence.getS2(), rightIsNegated);
            return new BinarySentence(left, binarySentence.getConnective(), right);
        }
        else {
            UnarySentence unarySentence = (UnarySentence)sentence;
            boolean shouldNegate = unarySentence.isNegated;
            if (outterIsNegated && !(unarySentence.isSymbol() || unarySentence.isLiteral())) {
                if (unarySentence.nestedSentence != null && unarySentence.nestedSentence instanceof BinarySentence) {
                    unarySentence = new UnarySentence(((BinarySentence)unarySentence.nestedSentence).negate());
                }
                else if (unarySentence.nestedSentence != null && unarySentence.nestedSentence instanceof UnarySentence) {
                    unarySentence = ((UnarySentence)unarySentence.nestedSentence).negate();
                }
                else {
                    unarySentence = unarySentence.negate();
                }
            }
            
            if (unarySentence.isSymbol() || unarySentence.isLiteral()) {
                // a || ~a
                return unarySentence;
            }
            else if (unarySentence.nestedUnary != null) {
                // ~(a)
                return eliminateNot(unarySentence.nestedUnary, shouldNegate);
            }
            else {
                // (a)
                return new UnarySentence(eliminateNot(unarySentence.nestedSentence, shouldNegate));
            }
        }
    }

    private Sentence applyDistributivity(Sentence sentence) {
        if (sentence instanceof BinarySentence) {
            BinarySentence binarySentence = (BinarySentence)sentence;
            if (binarySentence.getConnective() == BinaryConnective.OR) {
                UnarySentence s1 = binarySentence.getS1();
                UnarySentence s2 = binarySentence.getS2();
                if ((BinarySentence)s1.nestedSentence != null && ((BinarySentence)s1.nestedSentence).getConnective() == BinaryConnective.AND) {
                    //(a ^ b) v c == (a v c) ^ (b v c)
                    UnarySentence a = (UnarySentence)applyDistributivity(((BinarySentence)s1.nestedSentence).getS1());
                    UnarySentence b = (UnarySentence)applyDistributivity(((BinarySentence)s1.nestedSentence).getS2());
                    UnarySentence c = (UnarySentence)applyDistributivity(s2);

                    BinarySentence innerLeft = new BinarySentence(a, BinaryConnective.OR, c);
                    UnarySentence left = new UnarySentence(innerLeft);
                    BinarySentence innerRight = new BinarySentence(b, BinaryConnective.OR, c);
                    UnarySentence right = new UnarySentence(innerRight);
                    return new BinarySentence(left, BinaryConnective.AND, right);
                }
                else if ((BinarySentence)s2.nestedSentence != null && ((BinarySentence)s2.nestedSentence).getConnective() == BinaryConnective.AND) {
                    // a v (b ^ c) == (a v b) ^ (a v c)
                    UnarySentence a = (UnarySentence)applyDistributivity(s1);
                    UnarySentence b = (UnarySentence)applyDistributivity(((BinarySentence)s2.nestedSentence).getS1());
                    UnarySentence c = (UnarySentence)applyDistributivity(((BinarySentence)s2.nestedSentence).getS2());
    
                    BinarySentence innerLeft = new BinarySentence(a, BinaryConnective.OR, b);
                    UnarySentence left = new UnarySentence(innerLeft);
                    BinarySentence innerRight = new BinarySentence(a, BinaryConnective.OR, c);
                    UnarySentence right = new UnarySentence(innerRight);
                    return new BinarySentence(left, BinaryConnective.AND, right);
                }
            }
            UnarySentence left = (UnarySentence)applyDistributivity(binarySentence.getS1());
            UnarySentence right = (UnarySentence)applyDistributivity(binarySentence.getS2());
            return new BinarySentence(left, binarySentence.getConnective(), right);
        }
        else if (sentence instanceof UnarySentence) {
            UnarySentence unary = (UnarySentence)sentence;
            if (unary.nestedSentence != null) {
                unary.setNestedSentence(applyDistributivity(unary.nestedSentence));
                return unary;
            }
        }
        return sentence;
    }

    public void parse() {
        parse("Orig", 0);
    }

    public abstract void parse(String prefix, int indent);

    public Clauses deriveClauses() {
        return deriveClauses(this);
    }

    private Clauses deriveClauses(Sentence sentence) {
        Clauses result = new Clauses();
        if (sentence instanceof BinarySentence) {
            BinarySentence binary = (BinarySentence)sentence;
            UnarySentence left = (UnarySentence)binary.getS1();
            UnarySentence right = (UnarySentence)binary.getS2();


            if (binary.getConnective() == BinaryConnective.AND) {
                Clauses fromLeft = deriveClauses(left);
                Clauses fromRight = deriveClauses(right);
                if (fromLeft.size() != 0) {
                    result.addAll(fromLeft);
                }
                if (fromRight.size() != 0) {
                    result.addAll(fromRight);
                }
                return result;
            }
            else {
                Clauses fromLeft = deriveClauses(left);
                Clauses fromRight = deriveClauses(right);
                Clause disjunction = new Clause();
                for (Clause c : fromLeft) {
                    disjunction.addAll(c);
                }
                for (Clause c : fromRight) {
                    disjunction.addAll(c);
                }
                return new Clauses(disjunction);
            }
        }
        else if (sentence instanceof UnarySentence) {
            UnarySentence unary = (UnarySentence)sentence;
            if (unary.nestedSentence != null) {
                return deriveClauses(unary.nestedSentence);
            }
            if (unary.isLiteral()) {
                result.add(new Clause(unary));
            }
        }
        return result;
    }
}
