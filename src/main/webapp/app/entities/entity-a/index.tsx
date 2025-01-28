import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import EntityA from './entity-a';
import EntityADetail from './entity-a-detail';
import EntityAUpdate from './entity-a-update';
import EntityADeleteDialog from './entity-a-delete-dialog';

const EntityARoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<EntityA />} />
    <Route path="new" element={<EntityAUpdate />} />
    <Route path=":id">
      <Route index element={<EntityADetail />} />
      <Route path="edit" element={<EntityAUpdate />} />
      <Route path="delete" element={<EntityADeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default EntityARoutes;
