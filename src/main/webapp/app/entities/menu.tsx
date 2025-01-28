import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/entity-a">
        Entity A
      </MenuItem>
      <MenuItem icon="asterisk" to="/entity-b">
        Entity B
      </MenuItem>
      <MenuItem icon="asterisk" to="/entity-history">
        Entity History
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
