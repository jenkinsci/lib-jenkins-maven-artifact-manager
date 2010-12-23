package hudson.maven.artifact;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl </a>
 * @version $Id: AbstractArtifactComponentTestCase.java,v 1.5 2004/10/23 13:33:59
 *          jvanzyl Exp $
 */
public abstract class AbstractArtifactComponentTestCase
    extends PlexusTestCase
{
    protected abstract String component();

    

    protected String getRepositoryLayout()
    {
        return "default";
    }

    protected ArtifactRepository localRepository()
        throws Exception
    {
        String path = "target/test-classes/repositories/" + component() + "/local-repository";

        File f = new File( getBasedir(), path );

        ArtifactRepositoryLayout repoLayout = (ArtifactRepositoryLayout) lookup( ArtifactRepositoryLayout.ROLE,
                                                                                 "default" );

        return new DefaultArtifactRepository( "local", "file://" + f.getPath(), repoLayout );
    }

    protected ArtifactRepository remoteRepository()
        throws Exception
    {
        String path = "target/test-classes/repositories/" + component() + "/remote-repository";

        File f = new File( getBasedir(), path );

        ArtifactRepositoryLayout repoLayout = (ArtifactRepositoryLayout) lookup( ArtifactRepositoryLayout.ROLE,
                                                                                 "default" );

        ArtifactRepository repo = new DefaultArtifactRepository( "test", "file://" + f.getPath(), repoLayout,
                                              new ArtifactRepositoryPolicy(), new ArtifactRepositoryPolicy() ){{
                                                  
                                              }

                                            @Override
                                            public boolean isUniqueVersion()
                                            {
                                                return false;
                                            }};
        
        return repo;
    }

    protected ArtifactRepository badRemoteRepository()
        throws Exception
    {
        ArtifactRepositoryLayout repoLayout = (ArtifactRepositoryLayout) lookup( ArtifactRepositoryLayout.ROLE,
                                                                                 "default" );

        return new DefaultArtifactRepository( "test", "http://foo.bar/repository", repoLayout );
    }

    protected void assertRemoteArtifactPresent( Artifact artifact )
        throws Exception
    {
        ArtifactRepository remoteRepo = remoteRepository();

        String path = remoteRepo.pathOf( artifact );

        File file = new File( remoteRepo.getBasedir(), path );

        file = new File(file.getParentFile(), "artifact-1.0-SNAPSHOT.jar");
        
        if ( !file.exists() )
        {
            fail( "Remote artifact " + file + " should be present." );
        }
    }


    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected List remoteRepositories()
        throws Exception
    {
        List remoteRepositories = new ArrayList();

        remoteRepositories.add( remoteRepository() );

        return remoteRepositories;
    }

    // ----------------------------------------------------------------------
    // Test artifact generation for unit tests
    // ----------------------------------------------------------------------


    protected Artifact createArtifact( String artifactId, String version )
        throws Exception
    {
        return createArtifact( artifactId, version, "jar" );
    }

    protected Artifact createArtifact( String artifactId, String version, String type )
        throws Exception
    {
        return createArtifact( "org.apache.maven", artifactId, version, type );
    }

    protected Artifact createArtifact( String groupId, String artifactId, String version, String type )
        throws Exception
    {
        ArtifactFactory artifactFactory = (ArtifactFactory) lookup( ArtifactFactory.ROLE );

        return artifactFactory.createBuildArtifact( groupId, artifactId, version, type );
    }

}

