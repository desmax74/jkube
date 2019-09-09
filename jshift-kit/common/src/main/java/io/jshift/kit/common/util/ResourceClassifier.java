/**
 * Copyright 2016 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package io.jshift.kit.common.util;
/**
 * @author roland
 * @since 03/06/16
 */
public enum ResourceClassifier {

    OPENSHIFT("openshift"),
    KUBERNETES("kubernetes"),
    KUBERNETES_TEMPLATE("k8s-template");

    private final String classifier;

    ResourceClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getValue() {
        return classifier;
    }
}