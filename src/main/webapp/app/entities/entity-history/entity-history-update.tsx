import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedBlobField, ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { Action } from 'app/shared/model/enumerations/action.model';
import { createEntity, getEntity, reset, updateEntity } from './entity-history.reducer';

export const EntityHistoryUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const entityHistoryEntity = useAppSelector(state => state.entityHistory.entity);
  const loading = useAppSelector(state => state.entityHistory.loading);
  const updating = useAppSelector(state => state.entityHistory.updating);
  const updateSuccess = useAppSelector(state => state.entityHistory.updateSuccess);
  const actionValues = Object.keys(Action);

  const handleClose = () => {
    navigate('/entity-history');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.entityId !== undefined && typeof values.entityId !== 'number') {
      values.entityId = Number(values.entityId);
    }
    values.creationDate = convertDateTimeToServer(values.creationDate);

    const entity = {
      ...entityHistoryEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          creationDate: displayDefaultDateTime(),
        }
      : {
          actionType: 'CREATE',
          ...entityHistoryEntity,
          creationDate: convertDateTimeFromServer(entityHistoryEntity.creationDate),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jhipsterListenerApp.entityHistory.home.createOrEditLabel" data-cy="EntityHistoryCreateUpdateHeading">
            Create or edit a Entity History
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField name="id" required readOnly id="entity-history-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="User Login"
                id="entity-history-userLogin"
                name="userLogin"
                data-cy="userLogin"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Entity Name"
                id="entity-history-entityName"
                name="entityName"
                data-cy="entityName"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Entity Id"
                id="entity-history-entityId"
                name="entityId"
                data-cy="entityId"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <ValidatedField label="Action Type" id="entity-history-actionType" name="actionType" data-cy="actionType" type="select">
                {actionValues.map(action => (
                  <option value={action} key={action}>
                    {action}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedBlobField label="Content" id="entity-history-content" name="content" data-cy="content" openActionLabel="Open" />
              <ValidatedField
                label="Creation Date"
                id="entity-history-creationDate"
                name="creationDate"
                data-cy="creationDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/entity-history" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default EntityHistoryUpdate;
