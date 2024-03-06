package se.ifmo.ru.soa2024_lab3_service1.storage.repository;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import se.ifmo.ru.soa2024_lab3_service1.service.model.TicketType;
import se.ifmo.ru.soa2024_lab3_service1.storage.model.Filter;
import se.ifmo.ru.soa2024_lab3_service1.storage.model.Page;
import se.ifmo.ru.soa2024_lab3_service1.storage.model.Sort;
import se.ifmo.ru.soa2024_lab3_service1.storage.model.TicketEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class TicketRepositoryImpl implements TicketRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TicketEntity findById(int id){



        return entityManager.find(TicketEntity.class, id);
    }

    @Override
    @Transactional
    public TicketEntity save(TicketEntity entity) {
        if (entity == null) {
            return null;
        }
        entity.setCreationDate(LocalDate.now()); //TODO: CHECK IF ITS RIGHT

        return entityManager.merge(entity);
    }

    @Override
    @Transactional
    public boolean deleteById(int id) {
        return entityManager.createQuery("delete from TicketEntity f where f.id=:id")
                .setParameter("id", id)
                .executeUpdate() != 0;
    }

    @Override
    public Page<TicketEntity> getSortedAndFilteredPage(List<Sort> sortList, List<Filter> filters, Integer page, Integer size) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TicketEntity> flatsQuery = criteriaBuilder.createQuery(TicketEntity.class);
        Root<TicketEntity> root = flatsQuery.from(TicketEntity.class);
        CriteriaQuery<TicketEntity> select = flatsQuery.select(root);

        List<Predicate> predicates = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(filters)) {
            predicates = new ArrayList<>();

            for (Filter filter : filters) {
                switch (filter.getFilteringOperation()) {
                    case EQ:
                        predicates.add(criteriaBuilder.equal(
                                        root.get(filter.getFieldName()),
                                        getTypedFieldValue(filter.getFieldName(), filter.getFieldValue())
                                )
                        );
                        break;
                    case NEQ:
                        predicates.add(criteriaBuilder.notEqual(
                                        root.get(filter.getFieldName()),
                                        getTypedFieldValue(filter.getFieldName(), filter.getFieldValue())
                                )
                        );
                        break;
                    case GT:
                        predicates.add(criteriaBuilder.greaterThan(
                                        root.get(filter.getFieldName()),
                                        filter.getFieldValue()
                                )
                        );
                        break;
                    case LT:
                        predicates.add(criteriaBuilder.lessThan(
                                        root.get(filter.getFieldName()),
                                        filter.getFieldValue()
                                )
                        );
                        break;
                    case GTE:
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                                        root.get(filter.getFieldName()),
                                        filter.getFieldValue()
                                )
                        );
                        break;
                    case LTE:
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(
                                        root.get(filter.getFieldName()),
                                        filter.getFieldValue()
                                )
                        );
                        break;
                    case UNDEFINED:
                        break;
                }
            }

            select.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        }

        if (CollectionUtils.isNotEmpty(sortList)) {
            List<Order> orderList = new ArrayList<>();
            for (Sort sortItem : sortList) {
                if (sortItem.isDesc()) {
                    orderList.add(criteriaBuilder.desc(root.get(sortItem.getFieldName())));
                } else {
                    orderList.add(criteriaBuilder.asc(root.get(sortItem.getFieldName())));
                }
            }
            select.orderBy(orderList);
        }

        TypedQuery<TicketEntity> typedQuery = entityManager.createQuery(select);

        Page<TicketEntity> ret = new Page<>();

        if (page != null && size != null) {
            typedQuery.setFirstResult((page - 1) * size);
            typedQuery.setMaxResults(size);

            long countResult = 0;

            if (CollectionUtils.isEmpty(predicates)) {
                Query queryTotal = entityManager.createQuery("Select count(f.id) from TicketEntity f");
                countResult = (long) queryTotal.getSingleResult();
            } else {
                CriteriaBuilder qb = entityManager.getCriteriaBuilder();

                CriteriaQuery<Long> cq = qb.createQuery(Long.class);

                cq.select(qb.count(cq.from(TicketEntity.class)));
                cq.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
                countResult = entityManager.createQuery(cq).getSingleResult();
            }

            ret.setPage(page);
            ret.setPageSize(size);
            ret.setTotalPages((int) Math.ceil((countResult * 1.0) / size));
            ret.setTotalCount(countResult);
        }

        ret.setObjects(typedQuery.getResultList());

        return ret;
    }
    @Override
    public TicketEntity getMinimumType(){
        if ((long) entityManager.createQuery("select count(f) from TicketEntity f where f.type=:type")
                .setParameter("type", TicketType.CHEAP).getSingleResult() != 0) {
            return (TicketEntity) entityManager.createQuery("select f from TicketEntity f where f.type=:type")
                    .setParameter("type", TicketType.CHEAP).setMaxResults(1).getSingleResult();
        } else if ((long) entityManager.createQuery("select count(f) from TicketEntity f where f.type=:type")
                .setParameter("type", TicketType.BUDGETARY).getSingleResult() != 0) {
            return (TicketEntity) entityManager.createQuery("select f from TicketEntity f where f.type=:type")
                    .setParameter("type", TicketType.BUDGETARY).setMaxResults(1).getSingleResult();
        } else if ((long) entityManager.createQuery("select count(f) from TicketEntity f where f.type=:type")
                .setParameter("type", TicketType.VIP).getSingleResult() != 0) {
            return (TicketEntity) entityManager.createQuery("select f from TicketEntity f where f.type=:type")
                    .setParameter("type", TicketType.VIP).setMaxResults(1).getSingleResult();
        }
        return null;
    }

    @Override
    public long countTicketByPrice(float price) {
        return (long) entityManager.createQuery("select count(f) from TicketEntity f where f.price=:price")
                .setParameter("price", price).getSingleResult();
    }
    @Override
    public List<TicketEntity> getTicketGreaterType(String ticketType){
        if (Objects.equals(ticketType, "cheap")) {
            return entityManager.createQuery("select f from TicketEntity f where f.type=:type_one or f.type=:type_two", TicketEntity.class)
                    .setParameter("type_one", TicketType.BUDGETARY).setParameter("type_two", TicketType.VIP).getResultList();
        } else if (Objects.equals(ticketType, "budgetary")) {
            return entityManager.createQuery("select f from TicketEntity f where f.type=:type_one", TicketEntity.class)
                    .setParameter("type_one", TicketType.VIP).getResultList();
        } else if (Objects.equals(ticketType, "vip")){
            return null;
        }
        return null;
    }

    private Object getTypedFieldValue(String fieldName, String fieldValue) {
        if (Objects.equals(fieldName, "newField")) {
            return Boolean.valueOf(fieldValue);
        } else {
            return fieldValue;
        }
    }

}
