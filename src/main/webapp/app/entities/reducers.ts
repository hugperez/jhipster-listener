import entityA from 'app/entities/entity-a/entity-a.reducer';
import entityB from 'app/entities/entity-b/entity-b.reducer';
import entityHistory from 'app/entities/entity-history/entity-history.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  entityA,
  entityB,
  entityHistory,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
