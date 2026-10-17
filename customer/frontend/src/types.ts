export enum GoodsType {
    CUCUMBER = "CUCUMBER",
    CARROTS = "CARROTS",
    TOBACCO = "TOBACCO",
    EGGS = "EGGS",
    POTATO = "POTATO",
    PLUMS = "PLUMS",
}

export interface MarketGoodsDto {
    goodsType: GoodsType;
    timestamp: string | null;
    price: number;
    quantity: number;
}