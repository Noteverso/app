export interface BaseProject {
  name: string;
  noteCount: number;
  isFavorite: 0 | 1;
  color: string;
}

export interface FullProject extends BaseProject {
  projectId: string;
}

export type NewProject = BaseProject
