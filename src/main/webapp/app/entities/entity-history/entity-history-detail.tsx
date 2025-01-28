import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, byteSize, openFile } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './entity-history.reducer';

export const EntityHistoryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const entityHistoryEntity = useAppSelector(state => state.entityHistory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="entityHistoryDetailsHeading">Entity History</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{entityHistoryEntity.id}</dd>
          <dt>
            <span id="userLogin">User Login</span>
          </dt>
          <dd>{entityHistoryEntity.userLogin}</dd>
          <dt>
            <span id="entityName">Entity Name</span>
          </dt>
          <dd>{entityHistoryEntity.entityName}</dd>
          <dt>
            <span id="entityId">Entity Id</span>
          </dt>
          <dd>{entityHistoryEntity.entityId}</dd>
          <dt>
            <span id="actionType">Action Type</span>
          </dt>
          <dd>{entityHistoryEntity.actionType}</dd>
          <dt>
            <span id="content">Content</span>
          </dt>
          <dd>
            {entityHistoryEntity.content ? (
              <div>
                {entityHistoryEntity.contentContentType ? (
                  <a onClick={openFile(entityHistoryEntity.contentContentType, entityHistoryEntity.content)}>Open&nbsp;</a>
                ) : null}
                <span>
                  {entityHistoryEntity.contentContentType}, {byteSize(entityHistoryEntity.content)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="creationDate">Creation Date</span>
          </dt>
          <dd>
            {entityHistoryEntity.creationDate ? (
              <TextFormat value={entityHistoryEntity.creationDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/entity-history" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/entity-history/${entityHistoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default EntityHistoryDetail;
