/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.AttrValueBrandDTO;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.BrandDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AttrValueBrandDTOImpl;
import org.yes.cart.domain.dto.impl.BrandDTOImpl;
import org.yes.cart.domain.entity.AttrValueBrand;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.Brand;
import org.yes.cart.domain.entity.Etype;
import org.yes.cart.domain.entity.impl.AttrValueEntityBrand;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.FileService;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoBrandService;
import org.yes.cart.utils.HQLUtils;
import org.yes.cart.utils.impl.AttrValueDTOComparatorImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 ** User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoBrandServiceImpl
        extends AbstractDtoServiceImpl<BrandDTO, BrandDTOImpl, Brand>
        implements DtoBrandService {

    private static final AttrValueDTOComparatorImpl ATTR_VALUE_DTO_COMPARATOR = new AttrValueDTOComparatorImpl();

    private final Assembler attrValueAssembler;
    private final DtoAttributeService dtoAttributeService;
    private final GenericDAO<AttrValueEntityBrand, Long> attrValueEntityBrandDao;
    private final ImageService imageService;
    private final FileService fileService;
    private final SystemService systemService;


    /**
     * Construct base remote service.
     *
     * @param brandGenericService       {@link GenericService}
     * @param dtoFactory    {@link DtoFactory}
     * @param dtoAttributeService dto attribute service
     * @param attrValueEntityBrandDao attribute value service
     * @param imageService {@link ImageService} to manipulate  related images.
     * @param fileService {@link FileService} to manipulate related files
     * @param systemService system service
     */
    public DtoBrandServiceImpl(final GenericService<Brand> brandGenericService,
                               final DtoFactory dtoFactory,
                               final DtoAttributeService dtoAttributeService,
                               final GenericDAO<AttrValueEntityBrand, Long> attrValueEntityBrandDao,
                               final ImageService imageService,
                               final FileService fileService,
                               final AdaptersRepository adaptersRepository,
                               final SystemService systemService) {
        super(dtoFactory, brandGenericService, adaptersRepository);

        this.systemService = systemService;
        this.attrValueAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(AttrValueBrandDTO.class),
                brandGenericService.getGenericDao().getEntityFactory().getImplClass(AttrValueBrand.class)
        );
        this.dtoAttributeService = dtoAttributeService;
        this.attrValueEntityBrandDao = attrValueEntityBrandDao;
        this.imageService = imageService;
        this.fileService = fileService;
    }



    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    @Override
    public Class<BrandDTO> getDtoIFace() {
        return BrandDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    @Override
    public Class<BrandDTOImpl> getDtoImpl() {
        return BrandDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    @Override
    public Class<Brand> getEntityIFace() {
        return Brand.class;
    }

    /** {@inheritDoc}*/
    @Override
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<AttrValueBrandDTO> result = new ArrayList<>(getById(entityPk).getAttributes());
        final List<AttributeDTO> availableAttributeDTOs = dtoAttributeService.findAvailableAttributes(
                AttributeGroupNames.BRAND,
                getCodes(result));
        for (AttributeDTO attributeDTO : availableAttributeDTOs) {
            AttrValueBrandDTO attrValueBarndDTO = getAssemblerDtoFactory().getByIface(AttrValueBrandDTO.class);
            attrValueBarndDTO.setAttributeDTO(attributeDTO);
            attrValueBarndDTO.setBrandId(entityPk);
            result.add(attrValueBarndDTO);
        }
        result.sort(ATTR_VALUE_DTO_COMPARATOR);
        return result;
    }

    /** {@inheritDoc}*/
    @Override
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        final AttrValueEntityBrand valueEntityBrand = attrValueEntityBrandDao.findById(attrValueDTO.getAttrvalueId());
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntityBrand, getAdaptersRepository(), dtoFactory);
        attrValueEntityBrandDao.update(valueEntityBrand);
        return attrValueDTO;
        
    }

    /** {@inheritDoc}*/
    @Override
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {

        final Attribute atr = ((GenericService<Attribute>)dtoAttributeService.getService()).findById(attrValueDTO.getAttributeDTO().getAttributeId());
        final boolean multivalue = atr.isAllowduplicate();
        final Brand brand = service.findById(((AttrValueBrandDTO) attrValueDTO).getBrandId());
        if (!multivalue) {
            for (final AttrValueBrand avp : brand.getAttributes()) {
                if (avp.getAttributeCode().equals(atr.getCode())) {
                    // this is a duplicate, so need to update
                    attrValueDTO.setAttrvalueId(avp.getAttrvalueId());
                    return updateEntityAttributeValue(attrValueDTO);
                }
            }
        }

        AttrValueBrand valueEntityBrand = getPersistenceEntityFactory().getByIface(AttrValueBrand.class);
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntityBrand, getAdaptersRepository(), dtoFactory);
        valueEntityBrand.setAttributeCode(atr.getCode());
        valueEntityBrand.setBrand(brand);
        valueEntityBrand = attrValueEntityBrandDao.create((AttrValueEntityBrand) valueEntityBrand);
        attrValueDTO.setAttrvalueId(valueEntityBrand.getAttrvalueId());
        return attrValueDTO;
        

    }

    /** {@inheritDoc}*/
    @Override
    public long deleteAttributeValue(final long attributeValuePk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException{
        final AttrValueEntityBrand valueEntityBrand = attrValueEntityBrandDao.findById(attributeValuePk);
        final AttributeDTO attributeDTO = dtoAttributeService.findByAttributeCode(valueEntityBrand.getAttributeCode());
        if (Etype.IMAGE_BUSINESS_TYPE.equals(attributeDTO.getEtypeName())) {
            imageService.deleteImage(valueEntityBrand.getVal(),
                    Constants.BRAND_IMAGE_REPOSITORY_URL_PATTERN, systemService.getImageRepositoryDirectory());
        } else if (Etype.FILE_BUSINESS_TYPE.equals(attributeDTO.getEtypeName())) {
            fileService.deleteFile(valueEntityBrand.getVal(),
                    Constants.BRAND_FILE_REPOSITORY_URL_PATTERN, systemService.getFileRepositoryDirectory());
        }
        attrValueEntityBrandDao.delete(valueEntityBrand);
        return valueEntityBrand.getBrand().getBrandId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttrValueDTO createAndBindAttrVal(final long entityPk, final String attrName, final String attrValue)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnmappedInterfaceException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BrandDTO> findBrands(final String name) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<Brand> entities;
        if (StringUtils.isNotBlank(name)) {
            final String iName = HQLUtils.criteriaIlikeAnywhere(name);
            entities = service.getGenericDao().findByNamedQuery(
                    "BRAND.BY.GUID.NAME.DESCRIPTION.ORDBY.NAME",
                    iName, iName, iName
                    );
        } else {
            entities = service.findAll();
        }
        final List<BrandDTO> dtos = new ArrayList<>(entities.size());
        fillDTOs(entities, dtos);
        return dtos;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BrandDTO> findBy(final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final String iFilter = HQLUtils.criteriaIlikeAnywhere(filter);

        final List<Brand> entities = service.getGenericDao().findRangeByNamedQuery(
                "BRAND.BY.GUID.NAME.DESCRIPTION.ORDBY.NAME",
                page * pageSize, pageSize,
                iFilter, iFilter, iFilter);

        final List<BrandDTO> dtos = new ArrayList<>(entities.size());
        fillDTOs(entities, dtos);
        return dtos;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttrValueDTO getNewAttribute(final long entityPk) throws UnableToCreateInstanceException, UnmappedInterfaceException {
        final AttrValueBrandDTO dto = new AttrValueBrandDTOImpl();
        dto.setBrandId(entityPk);
        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getImageRepositoryUrlPattern() {
        return Constants.BRAND_IMAGE_REPOSITORY_URL_PATTERN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileRepositoryUrlPattern() {
        return Constants.BRAND_FILE_REPOSITORY_URL_PATTERN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSysFileRepositoryUrlPattern() {
        return Constants.BRAND_SYSFILE_REPOSITORY_URL_PATTERN;
    }

}
