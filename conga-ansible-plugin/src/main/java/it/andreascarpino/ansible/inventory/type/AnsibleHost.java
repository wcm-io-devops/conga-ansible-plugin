/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2016 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package it.andreascarpino.ansible.inventory.type;

/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Andrea Scarpino <me@andreascarpino.it>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
//CHECKSTYLE:OFF

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Strings;

/**
 * @author Andrea Scarpino
 */
@SuppressWarnings("hiding")
public class AnsibleHost {

  private String name;

  private Set<AnsibleVariable> variables;

  public AnsibleHost(String name) {
    super();
    this.name = name;
    this.variables = new HashSet<>();
  }

  public AnsibleHost(String name, List<AnsibleVariable> variables) {
    this(name);

    if (variables != null) {
      for (AnsibleVariable v : variables) {
        this.variables.add(v);
      }
    }
  }

  public String getName() {
    return name;
  }

  public Set<AnsibleVariable> getVariables() {
    return this.variables;
  }

  public void addVariable(AnsibleVariable variable) {
    this.variables.add(variable);
  }

  public void addVariables(List<AnsibleVariable> variables) {
    for (AnsibleVariable v : variables) {
      addVariable(v);
    }
  }

  public AnsibleVariable getVariable(String variableName) {
    for (AnsibleVariable v : variables) {
      if (v.getName().equals(variableName)) {
        return v;
      }
    }

    return null;
  }

  public void removeVariable(String variableName) {
    Iterator<AnsibleVariable> variableIterator = variables.iterator();
    while (variableIterator.hasNext()) {
      AnsibleVariable v = variableIterator.next();
      if (Strings.CS.equals(v.getName(), variableName)) {
        variableIterator.remove();
      }
    }
  }

  public void clear() {
    this.variables.clear();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if ((o == null) || (getClass() != o.getClass())) {
      return false;
    }

    AnsibleHost host = (AnsibleHost)o;

    return name.equals(host.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
