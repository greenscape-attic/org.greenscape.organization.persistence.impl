package org.greenscape.organization.persistence.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.greenscape.organization.OrganizationEntity;
import org.greenscape.organization.OrganizationModel;
import org.greenscape.organization.persistence.OrganizationPersistence;
import org.greenscape.persistence.DocumentModelBase;
import org.greenscape.persistence.PersistenceService;
import org.greenscape.persistence.annotations.Model;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class OrganizationPersistenceImpl implements OrganizationPersistence {

	private PersistenceService manager;

	@Override
	public OrganizationEntity findById(Object id) {
		return manager.findById(OrganizationEntity.class, id.toString());
	}

	@Override
	public OrganizationEntity findByOrganizationId(long orgId) {
		List<OrganizationEntity> list = manager.findByProperty(OrganizationEntity.class,
				OrganizationModel.ORGANIZATION_ID, orgId);
		if (list == null || list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public Collection<OrganizationEntity> findAll() {
		return manager.executeQuery(OrganizationEntity.class, "select * from "
				+ OrganizationEntity.class.getAnnotation(Model.class).name());
	}

	@Override
	public OrganizationEntity save(OrganizationEntity organizationEntity) {
		if (organizationEntity.getOrganizationId() != null && !organizationEntity.getOrganizationId().equals("")) {
			organizationEntity.setModifiedDate(new Date());
			manager.update(organizationEntity);
		} else {
			// get organization id
			String modelName = OrganizationEntity.class.getAnnotation(Model.class).name();

			if (manager.modelExists(modelName)) {
				List<?> result = (List<?>) manager.executeQuery("select max(" + OrganizationEntity.ORGANIZATION_ID
						+ ") as maxOrgId from " + modelName);
				if (result == null || result.isEmpty()) {
				} else {
					DocumentModelBase model = (DocumentModelBase) result.get(0);
				}
			} else {
			}
			Date today = new Date();
			organizationEntity.setModifiedDate(today);
			manager.save(organizationEntity);
		}
		return organizationEntity;
	}

	@Override
	public void deleteByOrganizationId(long orgId) {
		OrganizationEntity model = findByOrganizationId(orgId);
		manager.delete(model);
	}

	@Reference
	public void setPersistenceManager(PersistenceService manager) {
		this.manager = manager;
	}

	public void unsetPersistenceManager(PersistenceService manager) {
		this.manager = null;
	}

}
