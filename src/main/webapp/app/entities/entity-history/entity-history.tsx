import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { TextFormat, byteSize, getSortState, openFile } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './entity-history.reducer';

export const EntityHistory = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const entityHistoryList = useAppSelector(state => state.entityHistory.entities);
  const loading = useAppSelector(state => state.entityHistory.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="entity-history-heading" data-cy="EntityHistoryHeading">
        Entity Histories
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link to="/entity-history/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Entity History
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {entityHistoryList && entityHistoryList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('userLogin')}>
                  User Login <FontAwesomeIcon icon={getSortIconByFieldName('userLogin')} />
                </th>
                <th className="hand" onClick={sort('entityName')}>
                  Entity Name <FontAwesomeIcon icon={getSortIconByFieldName('entityName')} />
                </th>
                <th className="hand" onClick={sort('entityId')}>
                  Entity Id <FontAwesomeIcon icon={getSortIconByFieldName('entityId')} />
                </th>
                <th className="hand" onClick={sort('actionType')}>
                  Action Type <FontAwesomeIcon icon={getSortIconByFieldName('actionType')} />
                </th>
                <th className="hand" onClick={sort('content')}>
                  Content <FontAwesomeIcon icon={getSortIconByFieldName('content')} />
                </th>
                <th className="hand" onClick={sort('creationDate')}>
                  Creation Date <FontAwesomeIcon icon={getSortIconByFieldName('creationDate')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {entityHistoryList.map((entityHistory, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/entity-history/${entityHistory.id}`} color="link" size="sm">
                      {entityHistory.id}
                    </Button>
                  </td>
                  <td>{entityHistory.userLogin}</td>
                  <td>{entityHistory.entityName}</td>
                  <td>{entityHistory.entityId}</td>
                  <td>{entityHistory.actionType}</td>
                  <td>
                    {entityHistory.content ? (
                      <div>
                        {entityHistory.contentContentType ? (
                          <a onClick={openFile(entityHistory.contentContentType, entityHistory.content)}>Open &nbsp;</a>
                        ) : null}
                        <span>
                          {entityHistory.contentContentType}, {byteSize(entityHistory.content)}
                        </span>
                      </div>
                    ) : null}
                  </td>
                  <td>
                    {entityHistory.creationDate ? (
                      <TextFormat type="date" value={entityHistory.creationDate} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/entity-history/${entityHistory.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/entity-history/${entityHistory.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/entity-history/${entityHistory.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Entity Histories found</div>
        )}
      </div>
    </div>
  );
};

export default EntityHistory;
