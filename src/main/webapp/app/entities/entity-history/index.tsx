import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import EntityHistory from './entity-history';
import EntityHistoryDetail from './entity-history-detail';
import EntityHistoryUpdate from './entity-history-update';
import EntityHistoryDeleteDialog from './entity-history-delete-dialog';

const EntityHistoryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<EntityHistory />} />
    <Route path="new" element={<EntityHistoryUpdate />} />
    <Route path=":id">
      <Route index element={<EntityHistoryDetail />} />
      <Route path="edit" element={<EntityHistoryUpdate />} />
      <Route path="delete" element={<EntityHistoryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default EntityHistoryRoutes;
