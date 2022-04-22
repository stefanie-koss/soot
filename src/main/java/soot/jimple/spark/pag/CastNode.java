package soot.jimple.spark.pag;

import soot.AnySubType;
import soot.Context;
import soot.PhaseOptions;
import soot.RefType;
import soot.SootMethod;
import soot.Type;
import soot.options.CGOptions;

/**
 * Represents a type cast node (?) in the pointer assignment graph.
 * 
 * @author Stefanie Koß
 */
/**
 * This node is an auxiliary node which is inserted to wrap an AllocNode. It provides additional type information (via
 * getType()), all other methods return the result of the wrapped AllocNode.
 * 
 * Only type (and additional debug information) need to be handled. The type is computed when the node is created. Therefore,
 * getType() does not need to be overwritten. This would not work, if the type for AllocNodes would be updated for any
 * reason. A type change for the p2Node would cause the need of a type change for this cast node, too.
 * 
 * @author koss
 *
 */
public class CastNode extends AllocNode implements Context {
  /** Returns the new expression of this type cast site. */
  public Object getCastExpr() {
    return castExpr;
  }

  @Override
  public String toString() {
    return "CastNode " + getNumber() + " " + castExpr + " in method " + method;
  }

  /* End of public methods. */

  CastNode(PAG pag, Object castExpr, Type t, Node p2Node, SootMethod m) {
    super(pag, t, t, m);
    this.p2Node = p2Node;
    this.method = m;
    if (t instanceof RefType) {
      RefType rt = (RefType) t;
      if (rt.getSootClass().isAbstract()) {
        boolean usesReflectionLog = new CGOptions(PhaseOptions.v().getPhaseOptions("cg")).reflection_log() != null;
        if (!usesReflectionLog) {
          throw new RuntimeException("Attempt to create castnode with abstract type " + t);
        }
      }
    }
    this.castExpr = castExpr;
    if (castExpr instanceof ContextVarNode) {
      throw new RuntimeException();
    }

    // overapproximation for debugging
    this.type = new AnySubType(new RefType("java.lang.Object"));

    pag.getCastNodeNumberer().add(this);
  }

  public Node getP2Node() {
    return p2Node;
  }

  /* End of package methods. */

  protected Object castExpr;

  private SootMethod method;

  private Node p2Node;

  public SootMethod getMethod() {
    return method;
  }
}
