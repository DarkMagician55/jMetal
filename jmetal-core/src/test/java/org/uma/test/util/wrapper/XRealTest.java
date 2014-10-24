package org.uma.test.util.wrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.problem.multiobjective.Kursawe;
import org.uma.jmetal.util.JMetalException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Antonio J. Nebro on 28/03/14.
 */
public class XRealTest {
  XReal xreal_ ;
  Solution solution_ ;
  int problemVariables_ ;

  @Before
  public void setUp() throws ClassNotFoundException, JMetalException {
    Problem problem ;
    problemVariables_ = 5 ;

    problem = new Kursawe("Real", problemVariables_) ;
    solution_ = new Solution(problem) ;
    xreal_ = new XReal(solution_) ;
  }

  @After
  public void tearDown() throws Exception {
    xreal_ = null ;
  }

  @Test
  public void testConstructor() {
    //assertEquals("XRealTest.testConstructor", RealSolutionType.class, xreal_.getType_().getClass()) ;
  }

  @Test
  public void testCopyConstructor() {
    XReal xreal2 = new XReal(xreal_) ;
    //assertEquals("XRealTest.testCopyConstructor", xreal_.getType_().getClass(), xreal2.getType_().getClass()) ;
    assertEquals("XRealTest.testCopyConstructor", xreal_.size(), xreal2.size()) ;
  }

  @Test
  public void testCopyConstructor2() {
    XReal xreal2 = new XReal(solution_) ;
    //assertEquals("XRealTest.testCopyConstructor", xreal_.getType_().getClass(), xreal2.getType_().getClass()) ;
    assertEquals("XRealTest.testCopyConstructor", xreal_.size(), xreal2.size()) ;
  }

  @Test
  public void testSize() {
    assertEquals("XRealTest.testConstructor", problemVariables_, xreal_.size()) ;
  }
}
