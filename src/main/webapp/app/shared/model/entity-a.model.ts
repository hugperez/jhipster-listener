export interface IEntityA {
  id?: number;
  name?: string | null;
  title?: string | null;
  description?: string | null;
}

export const defaultValue: Readonly<IEntityA> = {};
