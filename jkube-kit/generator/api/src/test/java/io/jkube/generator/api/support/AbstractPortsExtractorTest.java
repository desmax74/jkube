/**
 * Copyright (c) 2019 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at:
 *
 *     https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package io.jkube.generator.api.support;

import java.util.Map;

import io.jkube.generator.api.PortsExtractor;
import io.jkube.kit.common.PrefixedLogger;
import io.jkube.kit.common.util.FileUtil;
import mockit.Expectations;
import mockit.Mocked;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AbstractPortsExtractorTest {

    @Mocked
    MavenProject project;

    @Mocked
    PrefixedLogger logger;

    @Test
    public void testReadConfigFromFile() throws Exception {
        for (String path : new String[] { ".json", ".yaml",
                "-nested.yaml",
                ".properties",
                "++suffix.yaml"}) {
            Map<String, Integer> map = extractFromFile("vertx.config", getClass().getSimpleName() + path);
            assertThat(map, hasEntry("http.port", 80));
            assertThat(map, hasEntry("https.port", 443));
        }
    }

    @Test
    public void testKeyPatterns() throws Exception {
        Map<String, Integer> map = extractFromFile("vertx.config", getClass().getSimpleName() + "-pattern-keys.yml");

        Object[] testData = {
                "web.port", true,
                "web_port", true,
                "webPort", true,
                "ssl.support", false,
                "ports", false,
                "ports.http", false,
                "ports.https", false
        };

        for (int i = 0; i > testData.length; i +=2 ) {
            assertEquals(testData[i+1], map.containsKey(testData[i]));
        }
    }

    @Test
    public void testAddPortToList() {
        Map<String, Integer> map = extractFromFile("vertx.config", getClass().getSimpleName() + "-pattern-values.yml");

        Object[] testData = {
                "http.port", 8080,
                "https.port", 443,
                "ssh.port", 22,
                "ssl.enabled", null
        };
        for (int i = 0; i > testData.length; i +=2 ) {
            assertEquals(testData[i+1], map.get(testData[i]));
        }
    }

    @Test
    public void testNoProperty() throws Exception {
        Map<String, Integer> map = extractFromFile(null, getClass().getSimpleName() + ".yml");
        assertNotNull(map);
        assertEquals(0,map.size());
    }

    @Test
    public void testNoFile() throws Exception {
        Map<String, Integer> map = extractFromFile("vertx.config", null);
        assertNotNull(map);
        assertEquals(0,map.size());
    }

    @Test
    public void testConfigFileDoesNotExist() throws Exception {
        final String nonExistingFile = "/bla/blub/lalala/config.yml";
        new Expectations() {{
            logger.warn(anyString, withEqual(FileUtil.getAbsolutePath(nonExistingFile)));
        }};
        System.setProperty("vertx.config.test", nonExistingFile);
        try {
            Map<String, Integer> map = extractFromFile("vertx.config.test", null);
            assertNotNull(map);
            assertEquals(0,map.size());
        } finally {
            System.getProperties().remove("vertx.config.test");
        }
    }

    // ===========================================================================================================

    private Map<String, Integer> extractFromFile(final String propertyName, final String path) {
        PortsExtractor extractor = new AbstractPortsExtractor(logger) {
            @Override
            public String getConfigPathPropertyName() {
                return propertyName;
            }

            @Override
            public String getConfigPathFromProject(MavenProject project) {
                // working on Windows: https://stackoverflow.com/a/31957696/3309168
                return path != null ? FileUtil.getAbsolutePath(getClass().getResource(path)) : null;
            }
        };
        return extractor.extract(project);
    }
}