//  RandomSearchSettingsTest.java
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
//  along with this program.  If not, see <http://www.gnu.org/licenses/>

package org.uma.test.experiment.settings;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.experiment.settings.RandomSearchSettings;
import org.uma.jmetal.metaheuristic.multiobjective.randomSearch.RandomSearch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 27/06/13
 * Time: 07:54
 * To change this template use File | Settings | File Templates.
 */
public class RandomSearchSettingsTest {
  Properties configuration;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("RandomSearch.conf").getPath()));
    configuration.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    Settings randomSettings = new RandomSearchSettings("Fonseca");
    RandomSearch algorithm = (RandomSearch)randomSettings.configure() ;

    assertEquals(25000, algorithm.getMaxEvaluations());
  }

  @Test
  public void testConfigure2() throws Exception {
    Settings randomSettings = new RandomSearchSettings("Fonseca");
    RandomSearch algorithm = (RandomSearch)randomSettings.configure(configuration) ;

    assertEquals(25000, algorithm.getMaxEvaluations());
  }
}
