/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.capedwarf.deployment;

import org.jboss.as.capedwarf.api.Constants;
import org.jboss.as.capedwarf.services.ServletExecutorConsumerService;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.msc.service.ServiceTarget;

/**
 * Define any MSC dependencies.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class CapedwarfDependenciesProcessor extends CapedwarfDeploymentUnitProcessor {

    protected void doDeploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        final ServiceTarget serviceTarget = phaseContext.getServiceTarget();
        serviceTarget.addDependency(ServletExecutorConsumerService.NAME); // we need queue -- as default gae queue is there by default
        // serviceTarget.addDependency(IndexingConsumerService.NAME); // we need indexing
        serviceTarget.addDependency(Constants.CHANNEL_BIND_INFO.getBinderServiceName()); // we need indexing
        serviceTarget.addDependency(Constants.EXECUTOR_BIND_INFO.getBinderServiceName()); // we need executor
        serviceTarget.addDependency(Constants.TF_BIND_INFO.getBinderServiceName()); // we need thread factory
    }

}
