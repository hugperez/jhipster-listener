import dayjs from 'dayjs';
import { Action } from 'app/shared/model/enumerations/action.model';

export interface IEntityHistory {
  id?: number;
  userLogin?: string;
  entityName?: string;
  entityId?: number;
  actionType?: keyof typeof Action | null;
  contentContentType?: string | null;
  content?: string | null;
  creationDate?: dayjs.Dayjs;
}

export const defaultValue: Readonly<IEntityHistory> = {};
