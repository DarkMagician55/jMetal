//  MOCHCTest.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.test.metaheuristic.multiobjective.mochc;

import org.junit.Test;
import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.experiment.settings.MOCHCSettings;

import static org.junit.Assert.assertTrue;

/**
 * Created by Antonio J. Nebro on 27/06/14.
 */
public class MOCHCTest {
  Algorithm algorithm ;

  @Test
  public void testNumberOfReturnedSolutionsInEasyProblem() throws Exception {
    algorithm = new MOCHCSettings("ZDT5").configure() ;

    SolutionSet solutionSet = algorithm.execute() ;
    /*
      Rationale: the default problem is ZDT4, and usually MOCHC; configured with standard
      settings should return more than 20 solutions
     */
    assertTrue(solutionSet.size() >= 20) ;
  }
}
