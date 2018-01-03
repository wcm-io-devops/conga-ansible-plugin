/*
 * Copyright 2016 - Fabio "MrWHO" Torchetti
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

package net.wedjaa.ansible.vault;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import net.wedjaa.ansible.vault.crypto.VaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class Manager
{
    Logger logger = LoggerFactory.getLogger(Manager.class);

    public Manager()
    {

    }

    public Object getFromYaml(Class objectClass, String yaml) throws YamlException
    {
        YamlReader reader = new YamlReader(new StringReader(yaml));
        return reader.read(objectClass);
    }

    public String writeToYaml(Object object) throws YamlException
    {
        StringWriter resultWriter = new StringWriter();
        YamlWriter writer = new YamlWriter(resultWriter);
        writer.write(object);
        writer.close();
        return resultWriter.getBuffer().toString();
    }

    public Object getFromVault(Class objectClass, String yaml, String password) throws IOException
    {
        byte [] clearYaml = VaultHandler.decrypt(yaml.getBytes(), password);
        return getFromYaml(objectClass, new String(clearYaml));
    }

    public String writeToVault(Object object, String password) throws IOException
    {
        String clearYaml = writeToYaml(object);
        byte [] yamlVault = VaultHandler.encrypt(clearYaml.getBytes(), password);
        return new String(yamlVault);
    }

}
