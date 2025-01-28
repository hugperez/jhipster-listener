import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import EntityA from './entity-a';
import EntityB from './entity-b';
import EntityHistory from './entity-history';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="entity-a/*" element={<EntityA />} />
        <Route path="entity-b/*" element={<EntityB />} />
        <Route path="entity-history/*" element={<EntityHistory />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
