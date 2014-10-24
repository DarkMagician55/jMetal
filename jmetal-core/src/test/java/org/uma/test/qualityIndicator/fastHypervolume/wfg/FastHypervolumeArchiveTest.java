//  FastHypervolumeArchiveTest.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro
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

package org.uma.test.qualityIndicator.fastHypervolume.wfg;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.qualityindicator.fasthypervolume.FastHypervolumeArchive;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 24/08/13
 * Time: 19:10
 */
public class FastHypervolumeArchiveTest {

  Comparator objectiveComparator_ ;

  @Before
  public void setup() {
    boolean descending ;
    objectiveComparator_  = new ObjectiveComparator(1, descending = true) ;
  }

  @Test
  public void Test1() {
    double epsilon = 0.00000000001 ;

    FastHypervolumeArchive archive = new FastHypervolumeArchive(4, 2) ;

    Solution sol1, sol2, sol3, sol4;
    sol1 = new Solution(2) ;
    sol1.setObjective(0, 4.6);
    sol1.setObjective(1, 8);
    sol2 = new Solution(2) ;
    sol2.setObjective(0, 5.35);
    sol2.setObjective(1, 7);
    sol3 = new Solution(2) ;
    sol3.setObjective(0, 6.7);
    sol3.setObjective(1, 6);
    sol4 = new Solution(2) ;
    sol4.setObjective(0, 8.9);
    sol4.setObjective(1, 5);

    archive.add(sol1) ;
    archive.add(sol2) ;
    archive.add(sol3) ;
    archive.add(sol4) ;

    archive.computeHVContribution();
//    assertEquals("Test 1", setArchive.referencePoint_.getObjective(0), 8.9, epsilon) ;
//    assertEquals("Test 1", setArchive.referencePoint_.getObjective(1)+10, 8.0, epsilon) ;

    //setArchive.sort(objectiveComparator_);
    //assertEquals("Test 1", 5.75, setArchive.get2DHV(), epsilon) ;
  }

  public void Test2() {
    double epsilon = 0.00000000001 ;

    FastHypervolumeArchive archive = new FastHypervolumeArchive(4, 2) ;

    Solution sol1, sol2, sol3, sol4;
    sol1 = new Solution(2) ;
    sol1.setObjective(0, 4.6);
    sol1.setObjective(1, 8);
    sol2 = new Solution(2) ;
    sol2.setObjective(0, 5.35);
    sol2.setObjective(1, 7);
    sol3 = new Solution(2) ;
    sol3.setObjective(0, 6.7);
    sol3.setObjective(1, 6);
    sol4 = new Solution(2) ;
    sol4.setObjective(0, 8.9);
    sol4.setObjective(1, 5);

    archive.add(sol1) ;
    archive.add(sol2) ;
    archive.add(sol3) ;
    archive.add(sol4) ;

    archive.computeHVContribution();
    assertEquals("Test 1", archive.referencePoint_.getObjective(0)+10.0, 8.9, epsilon) ;
    assertEquals("Test 1", archive.referencePoint_.getObjective(1)+10.0, 8.0, epsilon) ;
  }
}
