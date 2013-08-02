package org.jboss.as.capedwarf.deployment;

import io.undertow.server.ListenerRegistry;
import org.jboss.as.network.SocketBinding;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.web.common.WarMetaData;
import org.jboss.as.web.host.WebHost;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.msc.value.InjectedValue;
import org.wildfly.extension.io.IOServices;
import org.wildfly.extension.undertow.AbstractListenerService;
import org.wildfly.extension.undertow.Host;
import org.wildfly.extension.undertow.HttpListenerService;
import org.wildfly.extension.undertow.Server;
import org.wildfly.extension.undertow.UndertowService;
import org.xnio.Pool;
import org.xnio.XnioWorker;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author Stuart Douglas
 */
public class CapedwarfHostDeploymentProcessor extends CapedwarfDeploymentUnitProcessor {

    public static final String SERVER_NAME = "default";
    static final ServiceName REGISTRY_SERVICE_NAME = ServiceName.JBOSS.append("http", "listener", "registry");

    @Override
    protected void doDeploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        final DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
        ServiceTarget serviceTarget = phaseContext.getServiceTarget();

        int port = new Random().nextInt(1024) + 1024;
        System.out.println("PORT IS " + port);

        final String hostName = "capedwarfDynamic" + port;

        //first we add a host
        CapeDwarfHost host = new CapeDwarfHost(hostName, Collections.<String>singletonList("localhost"));
        serviceTarget.addService(CapeDwarfHost.SERVICE_NAME.append(hostName), host)
                .addDependency(UndertowService.SERVER.append(SERVER_NAME), Server.class, host.getServerInjection())
                .addDependency(UndertowService.UNDERTOW, UndertowService.class, host.getUndertowService())
                .addAliases(WebHost.SERVICE_NAME.append("localhost"))
                .install();


        HttpListenerService listener = new HttpListenerService(deploymentUnit.getName() + "cafedwarfListener", SERVER_NAME);
        final ServiceBuilder<? extends AbstractListenerService> serviceBuilder = serviceTarget.addService(deploymentUnit.getServiceName().append(CAPEDWARF_SERVICE_NAME).append("dynamicHttpListener"), listener);

        listener.getBinding().inject(new SocketBinding("tmp", port, true, null, 0, null, null, Collections.EMPTY_LIST)); //TODO: can these be null

        serviceBuilder.addDependency(IOServices.WORKER.append("default"), XnioWorker.class, listener.getWorker())
                .addDependency(IOServices.BUFFER_POOL.append("default"), Pool.class, listener.getBufferPool())
                .addDependency(UndertowService.SERVER.append(SERVER_NAME), Server.class, listener.getServerService())
                .addDependency(REGISTRY_SERVICE_NAME, ListenerRegistry.class, listener.getHttpListenerRegistry())
                .install();

        //now set the deployment to use our newly created host
        WarMetaData warMetaData = deploymentUnit.getAttachment(WarMetaData.ATTACHMENT_KEY);
        JBossWebMetaData merged = warMetaData.getMergedJBossWebMetaData();
        merged.getVirtualHosts().add(hostName);


    }

    private class CapeDwarfHost extends Host {

        protected CapeDwarfHost(String name, List<String> aliases) {
            super(name, aliases);
        }

        @Override
        protected InjectedValue<UndertowService> getUndertowService() {
            return super.getUndertowService();
        }

        @Override
        protected InjectedValue<Server> getServerInjection() {
            return super.getServerInjection();
        }
    }
}
