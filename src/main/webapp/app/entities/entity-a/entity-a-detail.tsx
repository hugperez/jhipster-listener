import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './entity-a.reducer';

export const EntityADetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const entityAEntity = useAppSelector(state => state.entityA.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="entityADetailsHeading">Entity A</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{entityAEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{entityAEntity.name}</dd>
          <dt>
            <span id="title">Title</span>
          </dt>
          <dd>{entityAEntity.title}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{entityAEntity.description}</dd>
        </dl>
        <Button tag={Link} to="/entity-a" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/entity-a/${entityAEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default EntityADetail;
