import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import EntityB from './entity-b';
import EntityBDetail from './entity-b-detail';
import EntityBUpdate from './entity-b-update';
import EntityBDeleteDialog from './entity-b-delete-dialog';

const EntityBRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<EntityB />} />
    <Route path="new" element={<EntityBUpdate />} />
    <Route path=":id">
      <Route index element={<EntityBDetail />} />
      <Route path="edit" element={<EntityBUpdate />} />
      <Route path="delete" element={<EntityBDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default EntityBRoutes;
