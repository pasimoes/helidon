/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.helidon.security.tools.config;

import io.helidon.config.Config;
import io.helidon.config.ConfigSources;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.helidon.common.CollectionsHelper.mapOf;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit test for secure config filter configured through configuration itself.
 */
public class SecureConfigFromConfigTest extends AbstractSecureConfigTest {
    private static Config config;
    private static Config configRequiresEncryption;

    @BeforeAll
    public static void initClass() {
        config = Config.create();

        configRequiresEncryption = Config.builder()
                .sources(ConfigSources.from(
                        //override require encryption
                        ConfigSources.from(mapOf(ConfigProperties.REQUIRE_ENCRYPTION_CONFIG_KEY, "true")),
                        ConfigSources.classpath("application.yaml")))
                .build();

        assertThat("We must have the correct configuration file", config.get("pwd1").type().isLeaf());
        assertThat("We must have the correct configuration file", configRequiresEncryption.get("pwd1").type().isLeaf());
    }

    @Override
    Config getConfig() {
        return config;
    }

    @Override
    Config getConfigRequiresEncryption() {
        return configRequiresEncryption;
    }

    @Test
    public void testSymmetricNoPassword() throws Exception {
        // these are expected not decrypted, as master password was not provided!
        testPassword(getConfigRequiresEncryption(),
                     "pwd4",
                     "${AES=YbaZGjQfwOv0htF2nmRYaOMYp0+qY/IRQUlWHfRKeTw6Q2uy33Rp8ZhTwv0oDywE}",
                     "symmetric");
        testPassword(getConfigRequiresEncryption(),
                     "pwd6",
                     "${AES=D/UgMzsNb265HU1NDvdzm7tACHdsW6u1PjYEcRkV/OLiWcI+ET6Q4MKCz0zHyEh9}",
                     "symmetric");
    }
}
