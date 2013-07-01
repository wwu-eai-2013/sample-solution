package de.java.ejb.drug.info;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import de.java.web.info.drug.DrugInfo;
import de.java.web.info.drug.DrugInfoResource;

@Stateless
public class DrugInfoServiceBean implements DrugInfoService {

  static final String PZN_SERVICE_HOST = "http://wi-eai.uni-muenster.de";

  private DrugInfoResource remote;

  @PostConstruct
  public void initialise() {
    RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
    remote = ProxyFactory.create(DrugInfoResource.class, PZN_SERVICE_HOST);
  }

  @Override
  public DrugInfo getInfo(int pzn) {
    try {
      return remote.getInfo(pzn);
    } catch (ClientResponseFailure e) {
      if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
        throw new UnknownDrugException("Could not find info for drug with pzn: " + pzn);
      }
      throw new EJBException("Could not retrieve information from third party webservice", e);
    }
  }

}
